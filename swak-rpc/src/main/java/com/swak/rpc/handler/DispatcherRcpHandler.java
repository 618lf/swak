package com.swak.rpc.handler;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.rpc.api.RpcRequest;
import com.swak.rpc.api.RpcResponse;
import com.swak.rpc.invoker.InvokeException;
import com.swak.rpc.invoker.Invoker;
import com.swak.rpc.invoker.InvokerMapping;

import reactor.core.publisher.Mono;

/**
 * 处理分发
 * @author lifeng
 */
public class DispatcherRcpHandler extends FilteringRpcHandler implements RpcHandler {

	private Logger logger = LoggerFactory.getLogger(DispatcherRcpHandler.class);
	
	// 查找并调用invoker
	private List<InvokerMapping> invokerMappings;
	
	public DispatcherRcpHandler(List<RpcFilter> filters, List<InvokerMapping> invokerMappings) {
		super(filters);
		this.invokerMappings = invokerMappings;
	}
	
    /**
     * 实际的处理 请求
     */
	@Override
	protected Mono<Void> doHandle(RpcRequest request, RpcResponse response) {
		Invoker<CompletableFuture<Object>> invoker = this.lookup(request);
		if (invoker == null) {
			return Mono.error(new InvokeException("no invoker found"));
		}
		return Mono.fromCompletionStage(invoker.invoke(request)).flatMap(v ->{
			response.setResult(v);
			return Mono.empty();
		});
	}
	
	private Invoker<CompletableFuture<Object>> lookup(RpcRequest request) {
		for(InvokerMapping mapping : invokerMappings) {
			Invoker<CompletableFuture<Object>> invoker = mapping.lookup(request);
			if (invoker != null) {
				return invoker;
			}
		}
		return null;
	}
	
	/**
	 * 错误处理
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 */
	protected Mono<Void> doError(RpcRequest request, RpcResponse response, Throwable e) {
		logger.error("{}", request.toString(), e);
		response.setException(e);
		return Mono.empty();
	}
	
	/**
	 * 执行 rpc filter  --> 处理请求
	 */
	@Override
	public Mono<Void> apply(RpcRequest request, RpcResponse response) {
		Mono<Void> completion = null;
		try {
			completion = super.doFilter(request, response);
		} catch (Throwable ex) {
			completion = Mono.error(ex);
		}
		return completion.onErrorResume(t -> doError(request, response, t));
	}
}