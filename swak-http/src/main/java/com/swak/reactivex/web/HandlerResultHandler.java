package com.swak.reactivex.web;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.reactivex.web.converter.HttpMessageConverter;
import com.swak.reactivex.web.result.HandlerResult;

import io.reactivex.Observable;

/**
 * 处理结果
 * @author lifeng
 */
public interface HandlerResultHandler {

	/**
	 * 是否支持
	 * @return
	 */
	boolean supports(HandlerResult result);
	
	/**
	 * 添加数据装换器
	 */
	void addConverter(HttpMessageConverter<?> messageConverter);
	
	/**
	 * 处理结果
	 * @param request
	 * @param response
	 * @param result
	 */
	Observable<Void> handle(HttpServerRequest request, HttpServerResponse response, HandlerResult result);
}