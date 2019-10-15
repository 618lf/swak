package com.swak.vertx.security.principal;

import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.Subject;
import com.swak.vertx.transport.Token;

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
	CompletionStage<Subject> createSubject(RoutingContext context);

	/**
	 * 创建
	 * 
	 * @param subject
	 * @return
	 */
	CompletionStage<Token> generateToken(Subject subject);
}
