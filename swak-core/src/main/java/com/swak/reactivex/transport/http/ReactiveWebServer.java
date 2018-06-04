package com.swak.reactivex.transport.http;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServerException;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.NettyPipeline;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.options.HttpServerOptions;
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

/**
 * 响应式的 http 服务器
 * 
 * @author lifeng
 */
public class ReactiveWebServer extends TcpServer implements WebServer {

	private static final Logger LOG = LoggerFactory.getLogger(ReactiveWebServer.class);

	private final HttpServerProperties properties;
	private BiFunction<NettyInbound, NettyOutbound, Mono<Void>> handler;
	private HttpServerOptions options;
	private String serverName; // 类似 http://127.0.0.1:8080
	private NettyContext context;
	private Thread shutdownHook;

	private ReactiveWebServer(HttpServerProperties properties) {
		this.properties = properties;
		this.options = this.options();
	}

	// ------------------ 启动服务器 ---------------------
	@Override
	@SuppressWarnings("unchecked")
	public void start(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler) {
		try {
			this.handler = (BiFunction<NettyInbound, NettyOutbound, Mono<Void>>)handler;
			this.context = this.start().subscribeOn(Schedulers.immediate()).doOnNext(ctx -> LOG.debug("Started {} on {}", "http-server", ctx.address()))
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

	/**
	 * 配置ssl
	 * 
	 * @param builder
	 */
	private void customizeSsl(HttpServerOptions.Builder options) {
		try {
			SslContext sslCtx = SslContextBuilder.forServer(new File(properties.getCertFilePath()),
					new File(properties.getPrivateKeyPath()), properties.getPrivateKeyPassword()).build();
			options.sslContext(sslCtx);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * 配置options
	 */
	private HttpServerOptions options(Consumer<? super HttpServerOptions.Builder> options) {
		HttpServerOptions.Builder serverOptionsBuilder = HttpServerOptions.builder();
		options.accept(serverOptionsBuilder);
		return serverOptionsBuilder.build();
	}

	// ---------------------- 初始化管道 -- 处理数据 ---------------------
	/**
	 * 管道初始化配置
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
	 * 创建 Channel 处理器
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
	 * Install a {@link Runtime#addShutdownHook(Thread) JVM shutdown hook} that will
	 * shutdown this {@link BlockingNettyContext} if the JVM is terminated
	 * externally.
	 * <p>
	 * The hook is removed if shutdown manually, and subsequent calls to this method
	 * are no-op.
	 */
	public void installShutdownHook() {
		// don't return the hook to discourage uninstalling it externally
		if (this.shutdownHook != null) {
			return;
		}
		this.shutdownHook = new Thread(this::shutdownFromJVM, "SWAK - ShutdownHook - jvm");
		Runtime.getRuntime().addShutdownHook(this.shutdownHook);
	}

	/**
	 * jvm 处罚关闭
	 */
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

	/**
	 * 程序触发关闭
	 * 
	 * @return
	 */
	public boolean removeShutdownHook() {
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
	
	@Override
	public int getPort() {
		return getAddress().getPort();
	}

	// ---------------------- 创建 http 服务器 ---------------------
	/**
	 * 创建 http 服务器
	 * 
	 * @param options
	 * @return
	 */
	public static ReactiveWebServer build(HttpServerProperties properties) {
		return new ReactiveWebServer(properties);
	}
}