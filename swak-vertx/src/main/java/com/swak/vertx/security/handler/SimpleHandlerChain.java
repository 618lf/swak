package com.swak.vertx.security.handler;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.Subject;

import io.vertx.ext.web.RoutingContext;

/**
 * 简单的执行链实现
 * 
 * @author lifeng
 */
public class SimpleHandlerChain implements HandlerChain {

	private final List<Handler> handlers;
	private int index = 0;

	public SimpleHandlerChain(List<Handler> handlers) {
		this.handlers = handlers;
	}

	/**
	 * 执行handler 链
	 */
	@Override
	public CompletionStage<Boolean> doHandler(RoutingContext context, Subject subject) {
		// 执行handler链， 如果有handler 返回false，就不回继续执行代码
		if (this.handlers != null && this.index < this.handlers.size()) {
			return this.handlers.get(this.index++).handle(context, subject, this);
		}

		// 所有的handler 都执行成功，则会返回true
		return CompletableFuture.completedFuture(true);
	}
}
