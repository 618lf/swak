package com.swak.vertx.security.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.Subject;

import io.vertx.ext.web.RoutingContext;

/**
 * 访问控制器
 * 
 * @author lifeng
 */
public abstract class AdviceHandler implements Handler {

	/**
	 * 执行处理
	 * 
	 * @param subject
	 * @return
	 */
	public CompletionStage<Boolean> handle(RoutingContext context, Subject subject, HandlerChain chain) {
		return this.isAccessDenied(context, subject).thenCompose(allowed -> {
			if (allowed) {
				return this.onAccessDenied(context, subject);
			}
			return CompletableFuture.completedFuture(allowed);
		});
	}

	/**
	 * 是否继续执行
	 * 
	 * @param subject
	 * @return
	 */
	public CompletionStage<Boolean> isAccessDenied(RoutingContext context, Subject subject) {
		return CompletableFuture.completedFuture(true);
	}

	/**
	 * 如果不继续执行则怎么处理
	 * 
	 * @param subject
	 * @return
	 */
	public CompletionStage<Boolean> onAccessDenied(RoutingContext context, Subject subject) {
		return CompletableFuture.completedFuture(true);
	}
}
