package com.swak.reactivex.transport.channel;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyPipeline;
import com.swak.reactivex.transport.options.ClientOptions;
import com.swak.reactivex.transport.options.NettyOptions;
import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/**
 * 定义 Channel 的生命周期接口
 * 
 * @author lifeng
 * @param <CHANNEL>
 */
public abstract class ContextHandler extends ChannelInitializer<Channel> {

	static final Logger log = LoggerFactory.getLogger(ContextHandler.class);
	final NettyOptions<?> options;
	final MonoSink<NettyContext> sink;
	ChannelOperations.OnNew channelOpFactory;
	BiConsumer<ChannelPipeline, ContextHandler> pipelineConfigurator;
	boolean fired;

	ContextHandler(NettyOptions<?> options, MonoSink<NettyContext> sink) {
		this.options = options;
		this.sink = sink;
	}

	/**
	 * @param channel
	 */
	protected void doStarted(Channel channel) {
		// ignore
	}
	
	/**
	 * One-time only future setter
	 * @param future
	 */
	public abstract void setFuture(Future<?> future);
	

	/**
	 * @param channel
	 */
	protected void doDropped(Channel channel) {
		// ignore
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		accept(ch);
	}

	/**
	 * Initialize pipeline and fire options event
	 *
	 * @param ch
	 *            channel to initialize
	 */
	public void accept(Channel channel) {
		try {
			doPipeline(channel);
			if (options.onChannelInit() != null) {
				if (options.onChannelInit().test(channel)) {
					if (log.isDebugEnabled()) {
						log.debug("DROPPED by onChannelInit predicate {}", channel);
					}
					doDropped(channel);
					return;
				}
			}
			if (pipelineConfigurator != null) {
				pipelineConfigurator.accept(channel.pipeline(), this);
			}
		} finally {
			if (null != options.afterChannelInit()) {
				options.afterChannelInit().accept(channel);
			}
		}
	}

	/**
	 * Trigger {@link MonoSink#success(Object)} that will signal
	 * {@link reactor.ipc.netty.NettyConnector#newHandler(BiFunction)} returned
	 * {@link Mono} subscriber.
	 *
	 * @param context
	 *            optional context to succeed the associated {@link MonoSink}
	 */
	public abstract void fireContextActive(NettyContext context);
	
	/**
	 * Trigger {@link MonoSink#error(Throwable)} that will signal
	 * {@link reactor.ipc.netty.NettyConnector#newHandler(BiFunction)} returned
	 * {@link Mono} subscriber.
	 *
	 * @param t
	 *            error to fail the associated {@link MonoSink}
	 */
	public void fireContextError(Throwable t) {
		if (!fired) {
			fired = true;
			sink.error(t);
		} else {
			log.error("Error cannot be forwarded to user-facing Mono", t);
		}
	}
	
	/**
	 * Initialize pipeline
	 *
	 * @param ch
	 *            channel to initialize
	 */
	protected void doPipeline(Channel ch) {
		this.addSslHandler(ch.pipeline());
	}

	/**
	 * 执行
	 * 
	 * @param ctx
	 * @param request
	 */
	public ChannelOperations<?,?> doChannel(Channel channel, Object request) {
		ChannelOperations<?,?> ops = channelOpFactory.create(channel, this, request);
		if (ops != null) {
			ops.onHandlerStart();
		}
		return ops;
	}

	/**
	 * 添加 ssl 的处理
	 * 
	 * @param secure
	 * @param pipeline
	 */
	public void addSslHandler(ChannelPipeline pipeline) {
		SslHandler sslHandler = options.getSslHandler(pipeline.channel().alloc());
		if (sslHandler != null) {
			pipeline.addFirst(NettyPipeline.SslHandler, sslHandler);
		}
	}

	/**
	 * 设置管道配置处理器
	 * 
	 * @param pipelineConfigurator
	 * @return
	 */
	public final ContextHandler onPipeline(BiConsumer<ChannelPipeline, ContextHandler> pipelineConfigurator) {
		this.pipelineConfigurator = Objects.requireNonNull(pipelineConfigurator, "pipelineConfigurator");
		return this;
	}

	/**
	 * 设置请求处理器
	 * 
	 * @param pipelineConfigurator
	 * @return
	 */
	public final ContextHandler onChannel(ChannelOperations.OnNew channelOpFactory) {
		this.channelOpFactory = Objects.requireNonNull(channelOpFactory, "channelOpFactory");
		return this;
	}

	/**
	 * Create a new server context
	 * 
	 * @param options
	 * @param sink
	 * @return
	 */
	public static ContextHandler newServerContext(ServerOptions options, MonoSink<NettyContext> sink) {
		return new ServerContextHandler(options, sink);
	}

	/**
	 * Create a new client context with optional pool support
	 * no pool
	 * @param options
	 * @param sink
	 * @return
	 */
	public static ContextHandler newClientContext(ClientOptions options, MonoSink<NettyContext> sink) {
		return new ClientContextHandler(options, sink, true);
	}
}
