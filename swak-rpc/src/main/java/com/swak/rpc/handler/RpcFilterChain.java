package com.swak.rpc.handler;

import com.swak.rpc.protocol.RpcRequest;
import com.swak.rpc.protocol.RpcResponse;

import reactor.core.publisher.Mono;

public interface RpcFilterChain {

	Mono<Void> filter(RpcRequest request, RpcResponse response);
}
