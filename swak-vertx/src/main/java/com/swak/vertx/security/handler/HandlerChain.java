package com.swak.vertx.security.handler;

import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.Subject;

import io.vertx.ext.web.RoutingContext;

/**
 * handler 执行链
 * 
 * @author lifeng
 */
public interface HandlerChain {

	/**
	 * 执行 handler
	 * 
	 * @return
	 */
	CompletionStage<Boolean> doHandler(RoutingContext context, Subject subject);
}
