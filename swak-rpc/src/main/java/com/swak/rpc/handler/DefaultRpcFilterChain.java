package com.swak.rpc.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.swak.rpc.protocol.RpcRequest;
import com.swak.rpc.protocol.RpcResponse;

import reactor.core.publisher.Mono;

/**
 * 默认的 filter chain
 * 
 * @author lifeng
 */
public class DefaultRpcFilterChain implements RpcFilterChain {
	
	private final FilteringRpcHandler handler;
	private final List<RpcFilter> filters;
	private int index;
	
	public DefaultRpcFilterChain(FilteringRpcHandler handler, RpcFilter ... filters) {
		this.filters = ObjectUtils.isEmpty(filters) ? Collections.emptyList() : Arrays.asList(filters);
		this.handler = handler;
		this.index = 0;
	}

	@Override
	public Mono<Void> filter(RpcRequest request, RpcResponse response) {
		if (this.index < this.filters.size()) {
			return this.filters.get(this.index++).filter(request, response, this);
		} else {
			return this.handler.doHandle(request, response);
		}
	}
}