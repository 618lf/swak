package com.swak.rpc.invoker;

import java.util.concurrent.CompletableFuture;

import com.swak.rpc.protocol.RpcRequest;

/**
 * 执行请求
 * @author lifeng
 */
public interface InvokerMapping {

	/**
	 * 执行请求，返回 CompletableFuture
	 * @param request
	 * @return
	 */
	<T> CompletableFuture<T> invoke(RpcRequest request);
}