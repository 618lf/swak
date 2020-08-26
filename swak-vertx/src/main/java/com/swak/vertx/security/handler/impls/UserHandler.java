package com.swak.vertx.security.handler.impls;

import java.util.concurrent.CompletableFuture;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.security.Subject;
import com.swak.vertx.security.Context;
import com.swak.vertx.security.handler.AdviceHandler;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * 需要用户是登录状态
 *
 * @author: lifeng
 * @date: 2020/3/29 20:39
 */
public class UserHandler extends AdviceHandler {

	/**
	 * 什么都不用判断，继续执行
	 */
	@Override
	public CompletableFuture<Boolean> isAccessDenied(Context context, Subject subject) {
		return CompletableFuture.completedFuture(subject.getPrincipal() != null);
	}

	/**
	 * 如果不继续执行则怎么处理
	 */
	@Override
	public CompletableFuture<Boolean> onAccessDenied(Context context, Subject subject) {
		context.header(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		context.end(Result.error(ErrorCode.NO_USER).toJson());
		return CompletableFuture.completedFuture(false);
	}
}
