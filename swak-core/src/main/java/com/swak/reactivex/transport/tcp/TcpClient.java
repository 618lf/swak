package com.swak.reactivex.transport.tcp;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.swak.reactivex.transport.NettyConnector;
import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.options.ClientOptions;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelPipeline;
import reactor.core.publisher.Mono;

/**
 * Tcp 连接
 * 
 * @author lifeng
 */
public abstract class TcpClient
		implements NettyConnector<NettyInbound, NettyOutbound>, BiConsumer<ChannelPipeline, ContextHandler> {

	/**
	 * 配置 ServerOptions
	 * 
	 * @return
	 */
	public abstract ClientOptions options();

	/**
	 * 创建一个连接器
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Mono<? extends NettyContext> connector(
			BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> ioHandler,
			InetSocketAddress address) {
		return Mono.create(sink -> {

			/**
			 * 配置信息
			 */
			ClientOptions options = options();

			/**
			 * init Handler
			 */
			ContextHandler contextHandler = ContextHandler.newClientContext(options, sink).onPipeline(this)
					.onChannel((c, ch, request) -> {
						return this.doHandler(c, ch, request,
								(BiFunction<NettyInbound, NettyOutbound, Mono<Void>>) ioHandler);
					});

			/**
			 * start client
			 */
			Bootstrap b = options.get().remoteAddress(address).handler(contextHandler);

			/**
			 * 链接
			 */
			contextHandler.setFuture(b.connect());
		});
	}
}