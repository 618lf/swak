package com.swak.reactivex.transport.http.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.springframework.boot.web.server.WebServerException;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.NettyPipeline;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.tcp.TcpServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class HttpServer extends TcpServer {

	private final HttpServerProperties properties;
	private BiFunction<NettyInbound, NettyOutbound, Mono<Void>> handler;
	private HttpServerOptions options;
	private String serverName; // 类似 http://127.0.0.1:8080
	private NettyContext context;
	private Thread shutdownHook;

	public HttpServer(HttpServerProperties properties) {
		this.properties = properties;
		this.options = this.options();
	}

	// ------------------ 启动服务器 ---------------------
	/**
	 * 启动服务器， 获取重要的 NettyContext
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void start(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler) {
		try {
			this.handler = (BiFunction<NettyInbound, NettyOutbound, Mono<Void>>)handler;
			this.context = this.connector().subscribeOn(Schedulers.immediate()).doOnNext(ctx -> LOG.debug("Started {} on {}", "http-server", ctx.address()))
					.block();
			this.startDaemonAwaitThread();
		} catch (Exception ex) {
			throw new WebServerException("Unable to start Netty", ex);
		}
	}

	/**
	 * 开启后台线程，等待服务器结束
	 * 
	 * @param nettyContext
	 */
	private void startDaemonAwaitThread() {
		Thread awaitThread = new Thread("SWAK-server-closeAwait") {
			@Override
			public void run() {
				context.onClose().block();
			}

		};
		awaitThread.setContextClassLoader(getClass().getClassLoader());
		awaitThread.setDaemon(false);
		awaitThread.start();
	}

	// ----------------- 配置服务器 ----------------------
	
	/**
	 * properties -> Options
	 */
	@Override
	public HttpServerOptions options() {
		if (options == null) {
			this.options = this.options((options) -> {
				options.host(properties.getHost()).port(properties.getPort());
				if (properties.isSslOn()) {
					this.customizeSsl(options);
				}
				options.serverName(properties.getName());
				options.transportMode(properties.getMode());
				options.serverSelect(properties.getServerSelect());
				options.serverWorker(properties.getServerWorker());
				options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout());
				options.childOption(ChannelOption.SO_KEEPALIVE, properties.isSoKeepAlive());
				options.childOption(ChannelOption.TCP_NODELAY, properties.isTcpNoDelay());
			});
			
			this.serverName = new StringBuilder().append(this.options.sslContext() != null ? "https://": "http://")
					.append(this.options.getAddress().getHostString()).toString();
			if (this.options.getAddress().getPort() != 80) {
				this.serverName = new StringBuilder(this.serverName).append(":").append(this.options.getAddress().getPort()).toString();
			}
		}
		return this.options;
	}
	private void customizeSsl(HttpServerOptions.Builder options) {
		try {
			SslContext sslCtx = SslContextBuilder.forServer(new File(properties.getCertFilePath()),
					new File(properties.getPrivateKeyPath()), properties.getPrivateKeyPassword()).build();
			options.sslContext(sslCtx);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
	private HttpServerOptions options(Consumer<? super HttpServerOptions.Builder> options) {
		HttpServerOptions.Builder serverOptionsBuilder = HttpServerOptions.builder();
		options.accept(serverOptionsBuilder);
		return serverOptionsBuilder.build();
	}

	// ---------------------- 初始化管道 -- 处理数据 ---------------------
	/**
	 * TcpServer.BiConsumer -> ContextHandler.onPipeline(this)
	 */
	@Override
	public void accept(ChannelPipeline p, ContextHandler ch) {
		if (options.enabledCompression()) {
			p.addLast(NettyPipeline.HttpCompressor, new HttpContentCompressor());
		}
		p.addLast(NettyPipeline.HttpCodec, new HttpServerCodec(36192 * 2, 36192 * 8, 36192 * 16, false));
		p.addLast(NettyPipeline.HttpAggregator, new HttpObjectAggregator(Integer.MAX_VALUE));
		p.addLast(NettyPipeline.ChunkedWriter, new ChunkedWriteHandler());
		p.addLast(NettyPipeline.HttpServerHandler,  new HttpServerHandler(ch));
	}
	
	/**
	 * TcpServer.OnNew -> ContextHandler.onChannel(this)
	 */
	@Override
	public ChannelOperations<?, ?> create(Channel c, ContextHandler ch, Object request) {
		return HttpServerOperations.bind(c, handler, ch, serverName, (FullHttpRequest) request);
	}

	// ---------------------- 停止服务器 ---------------------
	/**
	 * 停止服务器
	 */
	public void stop() {
		if (context == null || context.isDisposed()) {
			return;
		}
		removeShutdownHook();
		context.dispose();
		context.onClose()
				.doOnError(e -> LOG.error("Stopped {} on {} with an error {}", properties.getName(), context.address(), e))
				.doOnTerminate(() -> LOG.info("Stopped {} on {}", properties.getName(), context.address())).block();
		context = null;
		
		// 关闭必要的资源
		this.options.getLoopResources().dispose();
	}

	// ---------------------- JVM ---------------------
	/**
	 * JVM Hook
	 */
	public void installShutdownHook() {
		// don't return the hook to discourage uninstalling it externally
		if (this.shutdownHook != null) {
			return;
		}
		this.shutdownHook = new Thread(this::shutdownFromJVM, "SWAK - ShutdownHook - jvm");
		Runtime.getRuntime().addShutdownHook(this.shutdownHook);
	}
	protected void shutdownFromJVM() {
		if (context.isDisposed()) {
			return;
		}
		final String hookDesc = Thread.currentThread().toString();

		context.dispose();
		context.onClose()
				.doOnError(e -> LOG.error("Stopped {} on {} with an error {} from JVM hook {}", properties.getName(),
						context.address(), e, hookDesc))
				.doOnTerminate(
						() -> LOG.info("Stopped {} on {} from JVM hook {}", properties.getName(), context.address(), hookDesc))
				.block();
	}
	protected boolean removeShutdownHook() {
		if (this.shutdownHook != null && Thread.currentThread() != this.shutdownHook) {
			Thread sdh = this.shutdownHook;
			this.shutdownHook = null;
			return Runtime.getRuntime().removeShutdownHook(sdh);
		}
		return false;
	}

	// ---------------------- 服务器地址 ---------------------
	@Override
	public InetSocketAddress getAddress() {
		return context.address();
	}
	
	// ---------------------- 创建 http 服务器 ---------------------
	/**
	 * 创建 http 服务器
	 * 
	 * @param options
	 * @return
	 */
	public static HttpServer build(HttpServerProperties properties) {
		return new HttpServer(properties);
	}
}
