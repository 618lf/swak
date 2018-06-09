package com.swak.rpc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.rpc.invoker.InvokerMapping;
import com.swak.rpc.protocol.RpcRequest;
import com.swak.rpc.protocol.RpcResponse;

import reactor.core.publisher.Mono;

/**
 * 处理分发
 * @author lifeng
 */
public class DispatcherRcpHandler extends FilteringRpcHandler implements RpcHandler {

	private Logger logger = LoggerFactory.getLogger(DispatcherRcpHandler.class);
	
	// 查找并调用invoker
	private InvokerMapping invokerMapping;
	
	public DispatcherRcpHandler(RpcFilter[] filters, InvokerMapping invokerMapping) {
		super(filters);
		this.invokerMapping = invokerMapping;
	}
	
    /**
     * 实际的处理 请求
     */
	@Override
	protected Mono<Void> doHandle(RpcRequest request, RpcResponse response) {
		return Mono.fromCompletionStage(invokerMapping.invoke(request)).flatMap(v ->{
			return Mono.empty();
		});
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