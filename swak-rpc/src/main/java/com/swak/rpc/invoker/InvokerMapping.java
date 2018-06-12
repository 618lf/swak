package com.swak.rpc.invoker;

import java.util.concurrent.CompletableFuture;

import com.swak.rpc.api.RpcRequest;

/**
 * 充当协议管理的作用
 * @author lifeng
 */
public interface InvokerMapping {

	/**
	 * 执行请求，返回 CompletableFuture
	 * @param request
	 * @return
	 */
	<T> Invoker<CompletableFuture<T>> lookup(RpcRequest request);
}