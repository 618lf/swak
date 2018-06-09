package com.swak.rpc.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.swak.reactivex.context.ServerException;
import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.NettyPipeline;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.resources.LoopResources;
import com.swak.reactivex.transport.tcp.TcpServer;
import com.swak.rpc.codec.RpcDecoder;
import com.swak.rpc.codec.RpcEncoder;
import com.swak.rpc.protocol.RpcRequest;
import com.swak.rpc.protocol.RpcResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * RPC 服务器
 * @author lifeng
 */
public class RpcServer extends TcpServer {

	private final RpcServerProperties properties;
	private RpcServerOptions options;
	private String serverName; // 类似 http://127.0.0.1:8080
	private NettyContext context;
	private Thread shutdownHook;
	
	public RpcServer(RpcServerProperties properties) {
		this.properties = properties;
		this.options = this.options();
	}
	
	// ------------------ 启动服务器 ---------------------
	@Override
	public void start(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler) {
		try {
			this.context = this.connector(handler, this.options.getAddress()).subscribeOn(Schedulers.immediate())
					.doOnNext(ctx -> LOG.debug("Started {} on {}", "http-server", ctx.address())).block();
			this.startDaemonAwaitThread();
		} catch (Exception ex) {
			this.stop();
			throw new ServerException("Unable to start Netty", ex);
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
	public RpcServerOptions options() {
		if (options == null) {
			this.options = this.options((options) -> {
				options.host(properties.getHost()).port(properties.getPort());
				if (properties.isSslOn()) {
					this.customizeSsl(options);
				}
				options.loopResources(LoopResources.create(properties.getMode(), properties.getServerSelect(),
						properties.getServerWorker(), properties.getName()));
				options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout());
				options.childOption(ChannelOption.SO_KEEPALIVE, properties.isSoKeepAlive());
				options.childOption(ChannelOption.TCP_NODELAY, properties.isTcpNoDelay());
				options.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1024 * 1024, 2048 * 1024));
			});

			this.serverName = new StringBuilder().append(this.options.sslContext() != null ? "https://" : "http://")
					.append(this.options.getAddress().getHostString()).toString();
			if (this.options.getAddress().getPort() != 80) {
				this.serverName = new StringBuilder(this.serverName).append(":")
						.append(this.options.getAddress().getPort()).toString();
			}
		}
		return this.options;
	}
	
	private void customizeSsl(RpcServerOptions.Builder options) {
		try {
			SslContext sslCtx = SslContextBuilder.forServer(new File(properties.getCertFilePath()),
					new File(properties.getPrivateKeyPath()), properties.getPrivateKeyPassword()).build();
			options.sslContext(sslCtx);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	private RpcServerOptions options(Consumer<? super RpcServerOptions.Builder> options) {
		RpcServerOptions.Builder serverOptionsBuilder = RpcServerOptions.builder();
		options.accept(serverOptionsBuilder);
		return serverOptionsBuilder.build();
	}
	
	// ---------------------- 初始化管道 -- 处理数据 ---------------------
	@Override
	public void accept(ChannelPipeline p, ContextHandler ch) {
		p.addLast(NettyPipeline.HttpCodec, new RpcDecoder());
		p.addLast(NettyPipeline.HttpAggregator, new RpcEncoder(RpcResponse.class));
		p.addLast(NettyPipeline.HttpServerHandler, new RpcServerHandler(ch));
	}
	
	@Override
	public ChannelOperations<?, ?> doHandler(Channel c, ContextHandler contextHandler, Object request,
			BiFunction<NettyInbound, NettyOutbound, Mono<Void>> ioHandler) {
		return RpcServerOperations.bind(c, ioHandler, contextHandler, (RpcRequest)request);
	}

	// ---------------------- 停止服务器 ---------------------

	@Override
	public void stop() {
		// 关闭必要的资源
		this.options.getLoopResources().dispose();

		// 关闭链接
		if (!(context == null || context.isDisposed())) {
			removeShutdownHook();
			context.dispose();
			context.onClose().doOnError(
					e -> LOG.error("Stopped {} on {} with an error {}", properties.getName(), context.address(), e))
					.doOnTerminate(() -> LOG.info("Stopped {} on {}", properties.getName(), context.address())).block();
			context = null;
		}
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
				.doOnTerminate(() -> LOG.info("Stopped {} on {} from JVM hook {}", properties.getName(),
						context.address(), hookDesc))
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
	public static RpcServer build(RpcServerProperties properties) {
		return new RpcServer(properties);
	}
}