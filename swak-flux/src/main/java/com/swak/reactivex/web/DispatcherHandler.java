package com.swak.reactivex.web;

import java.util.List;

import com.swak.reactivex.handler.WebHandler;
import com.swak.reactivex.transport.http.HttpConst;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.interceptor.HandlerInterceptor;
import com.swak.reactivex.web.interceptor.MappedInterceptor;
import com.swak.reactivex.web.result.HandlerResult;

import reactor.core.publisher.Mono;

/**
 * mvc 式的处理方式
 * 
 * @author lifeng
 */
public class DispatcherHandler implements WebHandler {

	private List<HandlerMapping> mappings;
	private List<HandlerInterceptor> interceptors;
	private List<HandlerAdapter> adapters;
	private List<HandlerResultHandler> resultHandlers;

	public List<HandlerMapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<HandlerMapping> mappings) {
		this.mappings = mappings;
	}

	public List<HandlerInterceptor> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<HandlerInterceptor> interceptors) {
		this.interceptors = interceptors;
	}

	public List<HandlerAdapter> getAdapters() {
		return adapters;
	}

	public void setAdapters(List<HandlerAdapter> adapters) {
		this.adapters = adapters;
	}

	public List<HandlerResultHandler> getResultHandlers() {
		return resultHandlers;
	}

	public void setResultHandlers(List<HandlerResultHandler> resultHandlers) {
		this.resultHandlers = resultHandlers;
	}

	/**
	 * 处理请求
	 */
	@Override
	public Mono<Void> handle(HttpServerRequest request, HttpServerResponse response) {
		ExecutionChain executionChain = handleMappering(request, response);
		
		/**
		 *  没有可用的 Handler 
		 */
		if (executionChain == null) {
			return Mono.error(HttpConst.NOT_FOUND_EXCEPTION);
		}
		
		/**
		 *  没有可用的 Interceptor
		 */
		else if(executionChain.getInterceptors() == null
				|| executionChain.getInterceptors().size() == 0) {
			return this.doHandler(request, response, executionChain.getHandler());
		}
		
		/**
		 *  有 handler 和 Interceptor
		 */
		return executionChain.applyPreHandle(request, response).flatMap(b -> {
			if (b) {
				return this.doHandler(request, response, executionChain.getHandler());
			}
			return Mono.empty();
		}).flatMap(result -> executionChain.applyPostHandle(request, response));
	}

	/**
	 * 获得执行链
	 * @param request
	 * @param response
	 * @return
	 */
	public ExecutionChain handleMappering(HttpServerRequest request, HttpServerResponse response) {
		for (HandlerMapping mapping : mappings) {
			ExecutionChain handler = getExecutionChain(mapping, request);
			if (handler != null) {
				return handler;
			}
		}
		return null;
	}

	private ExecutionChain getExecutionChain(HandlerMapping mapping, HttpServerRequest request) {
		Handler handler = mapping.getHandler(request);
		if (handler != null) {
			HandlerExecutionChain chain = new HandlerExecutionChain(handler);
			for (HandlerInterceptor interceptor : this.interceptors) {
				HandlerInterceptor _mapping = null;
				if (interceptor instanceof MappedInterceptor) {
					MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
					if (mappedInterceptor.matches(request)) {
						_mapping = mappedInterceptor.getInterceptor();
					}
				} else {
					_mapping = interceptor;
				}

				// 没有匹配
				if (_mapping == null) {
					continue;
				}

				// 匹配到了
				chain.addInterceptors(_mapping);
			}
			return chain;
		}
		return null;
	}
	
	/**
	 * 执行 handler
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 */
	public Mono<Void> doHandler(HttpServerRequest request, HttpServerResponse response, Handler handler) {
		HandlerResult result = this.invokeHandler(request, response, handler);
		return handleResult(request, response, result);
	}
	private HandlerResult invokeHandler(HttpServerRequest request, HttpServerResponse response, Handler handler) {
		for (HandlerAdapter handlerAdapter : this.adapters) {
			if (handlerAdapter.supports(handler)) {
				return handlerAdapter.handle(request, response, handler);
			}
		}
		return null;
	}
	private Mono<Void> handleResult(HttpServerRequest request, HttpServerResponse response, HandlerResult result) {
		for (HandlerResultHandler resultHandler : this.resultHandlers) {
			if (resultHandler.supports(result)) {
				return resultHandler.handle(request, response, result);
			}
		}
		return Mono.error(HttpConst.NOT_FOUND_EXCEPTION);
	}
}
