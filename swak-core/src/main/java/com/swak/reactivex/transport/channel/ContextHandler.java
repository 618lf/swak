package com.swak.reactivex.transport.channel;

import java.util.Objects;
import java.util.function.BiConsumer;

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
import reactor.core.publisher.MonoSink;

/**
 * 定义 Channel 的生命周期接口
 * 
 * @author lifeng
 * @param <CHANNEL>
 */
public abstract class ContextHandler extends ChannelInitializer<Channel> {

	static final Logger log = LoggerFactory.getLogger(ContextHandler.class);
	protected final NettyOptions<?> options;
	protected final MonoSink<NettyContext> sink;
	protected ChannelOperations.OnNew channelOpFactory;
	protected BiConsumer<ChannelPipeline, ContextHandler> pipelineConfigurator;
	protected boolean fired;

	protected ContextHandler(NettyOptions<?> options, MonoSink<NettyContext> sink) {
		this.options = options;
		this.sink = sink;
	}

	//------------------ 接入服务相关 --------------------
	/**
	 * 启动服务
	 * @param future
	 */
	public abstract void setFuture(Future<?> future);

	/**
	 * 启动服务
	 * @param channel
	 */
	protected void doStarted(Channel channel) {}
	
	
	/**
	 * 停止服务
	 * @param channel
	 */
	protected void doDropped(Channel channel) {}
	
	//------------------ 配置处理器 --------------------
	/**
	 * 如何配置通道 -- accept
	 * @param pipelineConfigurator
	 * @return
	 */
	public final ContextHandler onPipeline(BiConsumer<ChannelPipeline, ContextHandler> pipelineConfigurator) {
		this.pipelineConfigurator = Objects.requireNonNull(pipelineConfigurator, "pipelineConfigurator");
		return this;
	}

	/**
	 * 如何处理请求 -- doChannel
	 * @param channelOpFactory
	 * @return
	 */
	public final ContextHandler onChannel(ChannelOperations.OnNew channelOpFactory) {
		this.channelOpFactory = Objects.requireNonNull(channelOpFactory, "channelOpFactory");
		return this;
	}
	

	//------------------ 初始化通道 --------------------
	/**
	 * 初始化通道
	 */
	@Override
	protected void initChannel(Channel ch) throws Exception {
		accept(ch);
	}
	
	/**
	 * 配置通道
	 * @param channel
	 */
	protected void accept(Channel channel) {
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
	 * 通用配置
	 * @param ch
	 */
	protected void doPipeline(Channel ch) {
		this.addSslHandler(ch.pipeline());
	}
	
	/**
	 * 添加 SSL
	 * @param pipeline
	 */
	protected void addSslHandler(ChannelPipeline pipeline) {
		SslHandler sslHandler = options.getSslHandler(pipeline.channel().alloc());
		if (sslHandler != null) {
			pipeline.addFirst(NettyPipeline.SslHandler, sslHandler);
		}
	}
	
	//------------------ 执行处理 --------------------
	/**
	 * 实际的执行代码
	 * @param channel
	 * @param request
	 * @return
	 */
	public ChannelOperations<?,?> doChannel(Channel channel, Object request) {
		ChannelOperations<?,?> ops = channelOpFactory.create(channel, this, request);
		if (ops != null) {
			ops.onHandlerStart();
		}
		return ops;
	}
	
	/**
	 * 成功处理，例如 onHandlerStart
	 * @param context
	 */
	public void fireContextActive(NettyContext context) {}
	
	/**
	 * 失败处理， 例如 服务器启动失败
	 * @param t
	 */
	public void fireContextError(Throwable t) {}

	//------------------ 创建客户端 或 服务器 --------------------
	
	/**
	 * 服务器
	 * @param options
	 * @param sink
	 * @return
	 */
	public static ContextHandler newServerContext(ServerOptions options, MonoSink<NettyContext> sink) {
		return new ServerContextHandler(options, sink);
	}

	/**
	 * 客户端
	 * @param options
	 * @param sink
	 * @return
	 */
	public static ContextHandler newClientContext(ClientOptions options, MonoSink<NettyContext> sink) {
		return new ClientContextHandler(options, sink, true);
	}
}