package com.swak.rpc.handler;

import java.util.function.BiFunction;

import com.swak.rpc.protocol.RpcRequest;
import com.swak.rpc.protocol.RpcResponse;

import reactor.core.publisher.Mono;

/**
 * Rpc 处理接口
 * @author lifeng
 */
public interface RpcHandler extends BiFunction<RpcRequest, RpcResponse, Mono<Void>> {

	/**
	 * 处理请求返回可订阅对象
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Void> apply(RpcRequest request, RpcResponse response);
}
