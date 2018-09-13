package com.swak.vertx.security.handler;

import com.swak.vertx.security.Subject;

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
	boolean doHandler(RoutingContext context, Subject subject);
}
