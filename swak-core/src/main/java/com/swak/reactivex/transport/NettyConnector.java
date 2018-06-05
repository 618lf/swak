package com.swak.reactivex.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * 创建一个异步连接器
	 * @return
	 */
	Mono<? extends NettyContext> connector();
}
