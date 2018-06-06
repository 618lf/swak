package com.swak.reactivex.transport.tcp;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.swak.reactivex.transport.NettyConnector;
import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelPipeline;
import reactor.core.publisher.Mono;

/**
 * A TCP server connector.
 *
 * @author Stephane Maldini
 * @author Violeta Georgieva
 */
public abstract class TcpServer implements NettyConnector<NettyInbound, NettyOutbound>, 
         BiConsumer<ChannelPipeline, ContextHandler> {

	/**
	 * 配置 ServerOptions
	 * @return
	 */
	public abstract ServerOptions options();
	
	/**
	 * 异步启动服务器，并注册启动监听
	 * @param handler
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Mono<? extends NettyContext> connector(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> ioHandler, InetSocketAddress address) {
		return Mono.create(sink -> {
			
			/**
			 * 配置项
			 */
			ServerOptions options = options();
			
			/**
			 * init Handler
			 */
			ContextHandler contextHandler = ContextHandler.newServerContext(options, sink)
					.onPipeline(this).onChannel((c, ch, request) -> {
						return this.doHandler(c, ch, request, (BiFunction<NettyInbound, NettyOutbound, Mono<Void>>)ioHandler);
					});
			
			/**
			 * start server
			 */
			ServerBootstrap b = options.get()
					.localAddress(address)
					.childHandler(contextHandler);
			
			/**
			 * 监听启动过程
			 */
			contextHandler.setFuture(b.bind());
		});
	}
}