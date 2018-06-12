package com.swak.rpc.handler;

import com.swak.rpc.api.RpcRequest;
import com.swak.rpc.api.RpcResponse;

import reactor.core.publisher.Mono;

public interface RpcFilterChain {

	Mono<Void> filter(RpcRequest request, RpcResponse response);
}
