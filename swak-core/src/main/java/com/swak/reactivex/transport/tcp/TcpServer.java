package com.swak.reactivex.transport.tcp;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import reactor.core.publisher.Mono;

/**
 * A TCP server connector.
 * 
 * @author lifeng
 */
public abstract class TcpServer implements BiConsumer<ChannelPipeline, ContextHandler> {

	protected Logger LOG = LoggerFactory.getLogger(TcpServer.class);
	
	/**
	 * 配置 ServerOptions
	 * @return
	 */
	public abstract ServerOptions options();
	
	/**
	 * 启动服务器
	 * @param handler
	 */
	public abstract void start(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler);
	
	/**
	 * 异步启动服务器，并注册启动监听
	 * @param handler
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Mono<? extends NettyContext> connector(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> ioHandler) {
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
					.localAddress(options.getAddress())
					.childHandler(contextHandler);
			
			/**
			 * 监听启动过程
			 */
			contextHandler.setFuture(b.bind());
		});
	}
	
	/**
	 * handler 处理器
	 * @param ioHandler
	 * @return
	 */
	public abstract ChannelOperations<?, ?> doHandler(Channel c, ContextHandler contextHandler, Object msg, BiFunction<NettyInbound, NettyOutbound, Mono<Void>> ioHandler);
	
	/**
	 * 协议
	 * 
	 * @return
	 */
	public abstract String getProtocol();
	
	/**
	 * 服务器的地址
	 * @return
	 */
	public abstract InetSocketAddress getAddress();
	
	/**
	 * 停止服务器
	 */
	public abstract void stop();
}