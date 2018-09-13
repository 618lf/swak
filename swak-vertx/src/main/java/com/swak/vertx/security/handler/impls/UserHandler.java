package com.swak.vertx.security.handler.impls;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.vertx.security.Subject;
import com.swak.vertx.security.handler.AdviceHandler;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.ext.web.RoutingContext;

/**
 * 需要用户是登录状态
 * 
 * @author lifeng
 */
public class UserHandler extends AdviceHandler {

	/**
	 * 什么都不用判断，继续执行
	 * 
	 * @param subject
	 * @return
	 */
	public boolean isAccessDenied(RoutingContext context, Subject subject) {
		return subject.isUser();
	}

	/**
	 * 如果不继续执行则怎么处理
	 * 
	 * @param subject
	 * @return
	 */
	public boolean onAccessDenied(RoutingContext context, Subject subject) {
		context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		context.response().end(Result.error(ErrorCode.NO_USER).toJson());
		return false;
	}
}
