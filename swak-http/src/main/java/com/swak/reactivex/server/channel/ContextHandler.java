package com.swak.reactivex.server.channel;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.server.NettyContext;
import com.swak.reactivex.server.NettyPipeline;
import com.swak.reactivex.server.options.NettyOptions;
import com.swak.reactivex.server.options.ServerOptions;
import com.swak.reactivex.server.tcp.TcpServer.Sink;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;

/**
 * 定义 Channel 的生命周期接口
 * @author lifeng
 * @param <CHANNEL>
 */
public abstract class ContextHandler extends ChannelInitializer<Channel> {

	static final Logger log = LoggerFactory.getLogger(ContextHandler.class);
	final NettyOptions options;
	BiConsumer<ChannelPipeline, ContextHandler> pipelineConfigurator;
	
	ContextHandler(NettyOptions options) {
		this.options = options;
	}

	/**
	 * @param channel
	 */
	protected void doStarted(Channel channel) {
		// ignore
	}

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
	 * One-time only future setter
	 *
	 * @param future
	 *            the connect/bind future to associate with and cancel on dispose
	 */
	public abstract void setFuture(Future<?> future);
	
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
	 * 添加 ssl 的处理
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
	 * @param pipelineConfigurator
	 * @return
	 */
	public final ContextHandler onPipeline(BiConsumer<ChannelPipeline, ContextHandler> pipelineConfigurator) {
		this.pipelineConfigurator =
				Objects.requireNonNull(pipelineConfigurator, "pipelineConfigurator");
		return this;
	}
	
	/**
	 * Create a new server context
	 * @param sink
	 * @param options
	 * @param loggingHandler
	 * @param channelOpFactory
	 *
	 * @return a new {@link ContextHandler} for servers
	 */
	public static ContextHandler newServerContext(ServerOptions options, Sink<NettyContext> sink) {
		return new ServerContextHandler(options, sink);
	}
}
