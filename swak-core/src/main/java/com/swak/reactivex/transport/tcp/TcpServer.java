package com.swak.reactivex.transport.tcp;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.swak.reactivex.transport.NettyConnector;
import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.channel.ChannelOperations;
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
         BiConsumer<ChannelPipeline, ContextHandler>, ChannelOperations.OnNew {

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
	 * 启动服务
	 * @param handler
	 */
	public abstract void start(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler);
	
	/**
	 * 异步启动服务器，并注册启动监听
	 * @param handler
	 * @return
	 */
	@Override
	public Mono<? extends NettyContext> connector() {
		return Mono.create(sink -> {
			
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
		});
	}

	/**
	 * 停止服务器
	 */
	public abstract void stop();
}