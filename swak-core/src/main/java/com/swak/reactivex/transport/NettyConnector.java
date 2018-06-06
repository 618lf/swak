package com.swak.reactivex.transport;

import java.net.InetSocketAddress;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;

import io.netty.channel.Channel;
import reactor.core.publisher.Mono;

/**
 * Netty 连接器
 * @author lifeng
 *
 * @param <INBOUND>
 * @param <OUTBOUND>
 */
public interface NettyConnector<INBOUND extends NettyInbound, OUTBOUND extends NettyOutbound> {
	
	Logger LOG = LoggerFactory.getLogger(NettyConnector.class);
	
	/**
	 * 创建一个异步连接器, 需要提供 io 处理器和链接地址
	 * @param handler
	 * @param address
	 * @return
	 */
	Mono<? extends NettyContext> connector(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> ioHandler, InetSocketAddress address);
	
	/**
	 * handler 处理器
	 * @param ioHandler
	 * @return
	 */
	public abstract ChannelOperations<?, ?> doHandler(Channel c, ContextHandler contextHandler, Object msg, BiFunction<NettyInbound, NettyOutbound, Mono<Void>> ioHandler);
}
