package com.swak.vertx.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 处理一些异常信息
 * 
 * @author lifeng
 */
public class ErrorHandler implements Handler<RoutingContext> {

	private Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
	private int code;

	public ErrorHandler(int code) {
		this.code = code;
	}

	/**
	 * 处理异常信息
	 */
	@Override
	public void handle(RoutingContext context) {
		if (!context.response().ended() && !context.response().closed()) {
			if (code == 500) {
				this._500(context);
			} else if (code == 404) {
				this._400(context);
			}
		}
	}

	/**
	 * 打印异常
	 * 
	 * @param context
	 */
	private void _500(RoutingContext context) {
		context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		context.response().end(Result.error(ErrorCode.SERVER_ERROR).toJson());
		logger.error("{}", context.request().uri(), context.failure());
	}

	/**
	 * 仅仅输出异常即可
	 * 
	 * @param context
	 */
	private void _400(RoutingContext context) {
		context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		context.response().end(Result.error(ErrorCode.PATH_NOT_FOUNT).toJson());
	}
}
