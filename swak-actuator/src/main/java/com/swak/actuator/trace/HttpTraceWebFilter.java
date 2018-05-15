package com.swak.actuator.trace;

import org.springframework.core.Ordered;

import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebFilterChain;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 跟踪执行的路径
 * @author lifeng
 */
public class HttpTraceWebFilter implements WebFilter, Ordered {

	private final HttpTraceRepository traceRepository;
	
	public HttpTraceWebFilter(HttpTraceRepository traceRepository) {
		this.traceRepository = traceRepository;
	}
	
	/**
	 * 相关于一个后置处理器
	 */
	@Override
	public Mono<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain chain) {
		HttpTrace trace = this.traceRepository.receivedRequest(request);
		return chain.filter(request, response).doAfterSuccessOrError((aVoid, ex) ->{
			this.traceRepository.sendingResponse(trace, response);
		});
	}
	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}
}