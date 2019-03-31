package com.swak.flux.web;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.flux.web.converter.HttpMessageConverter;

import reactor.core.publisher.Mono;

/**
 * 处理结果
 * @author lifeng
 */
public interface HandlerResultHandler {

	/**
	 * 是否支持
	 * @return
	 */
	boolean supports(Object result);
	
	/**
	 * 添加数据装换器
	 */
	void addConverter(HttpMessageConverter messageConverter);
	
	/**
	 * 处理结果
	 * @param request
	 * @param response
	 * @param result
	 */
	Mono<Void> handle(HttpServerRequest request, HttpServerResponse response, Object result);
}