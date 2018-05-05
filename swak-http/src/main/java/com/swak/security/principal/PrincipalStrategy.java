package com.swak.security.principal;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.security.subject.Subject;

/**
 * 身份的存储策略
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
	void createPrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response);

	/**
	 * 将身份失效
	 * 
	 * @param request
	 * @param response
	 */
	void invalidatePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response);

	/**
	 * 获取身份
	 * 
	 * @param subject
	 * @param request
	 * @param response
	 */
	void resolvePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response);

	/**
	 * 将此 sessionId 对应的 身份失效
	 * 
	 * @param request
	 * @param response
	 */
	void invalidatePrincipal(String sessionId);
}
