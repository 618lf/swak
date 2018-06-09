package com.swak.rpc.handler;

import com.swak.rpc.protocol.RpcRequest;
import com.swak.rpc.protocol.RpcResponse;

import reactor.core.publisher.Mono;

public interface RpcFilter {

	
	/**
	 * filter 的顺序
	 * @return
	 */
	int getOrder();
	
	/**
	 * 处理请求返回可订阅对象
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Void> filter(RpcRequest request, RpcResponse response, RpcFilterChain chain);
}
