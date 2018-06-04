package com.swak.reactivex.transport.tcp;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
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
public abstract class TcpServer implements BiConsumer<ChannelPipeline, ContextHandler>{

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
	protected final Mono<? extends NettyContext> asyncStart(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler) {
		return Mono.create(sink ->{
			startWithSink(sink, handler);
		});
	}
	
	/**
	 * 异步启动服务，并注册启动通知 -- 相当于一个回调
	 * @param sink
	 * @param handler
	 */
	@SuppressWarnings("unchecked")
	protected void startWithSink(MonoSink<NettyContext> sink, BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler) {
		
		BiFunction<NettyInbound, NettyOutbound, Mono<Void>> _handler = (BiFunction<NettyInbound, NettyOutbound, Mono<Void>>)handler;
		
		/**
		 * init Handler
		 */
		ContextHandler contextHandler = this.newHandler(sink, _handler)
				.onPipeline(this);
		
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
	 * 创建实际的 ContextHandler
	 * @param sink
	 * @return
	 */
	protected abstract ContextHandler newHandler(MonoSink<NettyContext> sink, BiFunction<NettyInbound, NettyOutbound, Mono<Void>> handler);
	
	
	/**
	 * 停止服务器
	 */
	public abstract void stop();
}