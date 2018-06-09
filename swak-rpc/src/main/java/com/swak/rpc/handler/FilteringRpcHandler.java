package com.swak.rpc.handler;

import com.swak.rpc.protocol.RpcRequest;
import com.swak.rpc.protocol.RpcResponse;

import reactor.core.publisher.Mono;

/**
 * filter 过滤器
 * @author lifeng
 */
public abstract class FilteringRpcHandler {

	private final RpcFilter[] filters;
	
	public FilteringRpcHandler(RpcFilter[] filters) {
		this.filters = filters;
	}
	
	/**
	 * 执行filter 
	 * @param request
	 * @param response
	 * @return
	 */
	public Mono<Void> doFilter(RpcRequest request, RpcResponse response) {
		return this.filters.length != 0 ?
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