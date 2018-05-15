package com.swak.reactivex.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.common.exception.ErrorCode;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 默认的异常处理
 * @author lifeng
 */
public class DefaultWebExceptionHandler implements WebExceptionHandler{
	
	private Logger logger = LoggerFactory.getLogger(DefaultWebExceptionHandler.class);
	
	/**
	 * 打印异常信息
	 */
	@Override
	public Mono<Void> handle(HttpServerRequest request, HttpServerResponse response, Throwable ex) {
		logger.error("{}", request.getRequestURL(), ex);
		response.error().json().accept().buffer(ErrorCode.OPERATE_FAILURE.toJson());
		return Mono.empty();
	}
}