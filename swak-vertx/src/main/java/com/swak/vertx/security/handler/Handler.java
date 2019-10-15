package com.swak.vertx.security.handler;

import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.Subject;

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
	CompletionStage<Boolean> handle(RoutingContext context, Subject subject, HandlerChain chain);
}
