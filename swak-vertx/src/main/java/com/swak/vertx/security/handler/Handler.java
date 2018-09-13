package com.swak.vertx.security.handler;

import com.swak.vertx.security.Subject;

import io.vertx.ext.web.RoutingContext;

/**
 * 权限处理器
 * 
 * @author lifeng
 */
public interface Handler {
	
	/**
	 * handle 执行链获取的路径
	 */
	String CHAIN_RESOLVE_PATH = "CHAIN_RESOLVE_PATH";

	/**
	 * 执行处理
	 * 
	 * @param subject
	 * @return
	 */
	boolean handle(RoutingContext context, Subject subject, HandlerChain chain);
}
