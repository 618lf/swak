package com.swak.vertx.security.principal;

import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.Subject;

import io.vertx.ext.web.RoutingContext;

/**
 * 身份解析
 * 
 * @author lifeng
 */
public interface PrincipalStrategy {

	/**
	 * 创建身份
	 * 
	 * @param session
	 * @param request
	 * @param response
	 */
	CompletionStage<Subject> createPrincipal(RoutingContext context);

}
