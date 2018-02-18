package com.swak.security.principal;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.security.subjct.Subject;
/**
 * Session 的存储策略
 * @author lifeng
 */
public interface PrincipalStrategy {

	/**
	 * 创建身份
	 * @param session
	 * @param request
	 * @param response
	 */
	void createPrincipal(Subject subject, HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 将身份失效
	 * @param request
	 * @param response
	 */
	void invalidatePrincipal(Subject subject, HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 获取身份
	 * @param subject
	 * @param request
	 * @param response
	 */
	void resolvePrincipal(Subject subject, HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 将此 sessionId 对应的 身份失效
	 * @param request
	 * @param response
	 */
	void invalidatePrincipal(String sessionId);
}
