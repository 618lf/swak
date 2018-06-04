package com.swak.reactivex.transport.tcp;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.transport.ChannelHandler;
import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/**
 * A TCP server connector.
 *
 * @author Stephane Maldini
 * @author Violeta Georgieva
 */
public abstract class TcpServer implements BiConsumer<ChannelPipeline, ContextHandler>, ChannelHandler<Channel, Object>{

	/**
	 * 配置 ServerOptions
	 * @return
	 */
	public abstract ServerOptions options();
	
	/**
	 * 启动服务，并配置HttpHandler
	 * @param handler
	 */
	public abstract void start(HttpHandler handler);
	
	/**
	 * 停止服务器
	 */
	public abstract void stop();
	
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
	public final Mono<? extends NettyContext> asyncStart() {
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
}