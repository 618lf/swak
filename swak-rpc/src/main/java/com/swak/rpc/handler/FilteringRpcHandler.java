package com.swak.rpc.handler;

import java.util.List;

import com.swak.rpc.api.RpcRequest;
import com.swak.rpc.api.RpcResponse;

import reactor.core.publisher.Mono;

/**
 * filter 过滤器
 * @author lifeng
 */
public abstract class FilteringRpcHandler {

	private final List<RpcFilter> filters;
	
	public FilteringRpcHandler(List<RpcFilter> filters) {
		this.filters = filters;
	}
	
	/**
	 * 执行filter 
	 * @param request
	 * @param response
	 * @return
	 */
	public Mono<Void> doFilter(RpcRequest request, RpcResponse response) {
		return this.filters.size() != 0 ?
				new DefaultRpcFilterChain(this, this.filters).filter(request, response) :
				this.doHandle(request, response);
	}
	
	/**
	 * 执行实际的处理
	 * @param request
	 * @param response
	 * @return
	 */
	protected abstract Mono<Void> doHandle(RpcRequest request, RpcResponse response);
}