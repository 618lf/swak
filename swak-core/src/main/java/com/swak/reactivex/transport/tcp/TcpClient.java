package com.swak.reactivex.transport.tcp;

import java.util.function.BiConsumer;

import com.swak.reactivex.transport.NettyConnector;
import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.channel.ChannelOperations;
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
		implements NettyConnector<NettyInbound, NettyOutbound>, BiConsumer<ChannelPipeline, ContextHandler>, 
		ChannelOperations.OnNew {

	/**
	 * 配置 ServerOptions
	 * @return
	 */
	public abstract ClientOptions options();

	/**
	 * 创建一个连接器
	 */
	@Override
	public Mono<? extends NettyContext> connector() {
		return Mono.create(sink -> {
			
			/**
			 * init Handler
			 */
			ContextHandler contextHandler = ContextHandler.newClientContext(options(), sink)
					  .onPipeline(this).onChannel(this);

			/**
			 * start client
			 */
			Bootstrap b = options().get()
					  .remoteAddress(options().getAddress())
					  .handler(contextHandler);
			
			/**
			 * 链接
			 */
			contextHandler.setFuture(b.connect());
		});
	}
}