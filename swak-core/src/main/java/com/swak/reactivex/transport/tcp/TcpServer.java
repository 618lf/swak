package com.swak.reactivex.transport.tcp;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelPipeline;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/**
 * A TCP server connector.
 *
 * @author Stephane Maldini
 * @author Violeta Georgieva
 */
public abstract class TcpServer implements BiConsumer<ChannelPipeline, ContextHandler>, ChannelOperations.OnNew {

	/**
	 * 配置 ServerOptions
	 * @return
	 */
	public abstract ServerOptions options();
	
	/**
	 * 获得监听端口
	 * @return
	 */
	public abstract InetSocketAddress getAddress();
	
	/**
	 * 异步启动服务器，并注册启动监听
	 * @param handler
	 * @return
	 */
	public final Mono<? extends NettyContext> start() {
		return Mono.create(sink ->{
			startWithSink(sink);
		});
	}
	
	/**
	 * 异步启动服务，并注册启动通知 -- 相当于一个回调
	 * @param sink
	 * @param handler
	 */
	protected void startWithSink(MonoSink<NettyContext> sink) {
		
		/**
		 * init Handler
		 */
		ContextHandler contextHandler = ContextHandler.newServerContext(options(), sink)
				.onPipeline(this).onChannel(this);
		
		/**
		 * start server
		 */
		ServerBootstrap b = options().get()
				.localAddress(options().getAddress())
				.childHandler(contextHandler);
		
		/**
		 * 监听启动过程
		 */
		contextHandler.setFuture(b.bind());
	}
	
	/**
	 * 停止服务器
	 */
	public abstract void stop();
}