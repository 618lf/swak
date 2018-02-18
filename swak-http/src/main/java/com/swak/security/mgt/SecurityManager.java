package com.swak.security.mgt;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.principal.Principal;
import com.swak.security.subjct.Subject;

/**
 * 总体的控制器
 * @author lifeng
 */
public interface SecurityManager {

	// 权限
	boolean isPermitted(Subject subject, String permission);
	boolean[] isPermitted(Subject subject, String... permissions);
	boolean isPermittedAll(Subject subject, String... permissions);
	boolean hasRole(Subject subject, String role);
	boolean[] hasRoles(Subject subject, String... roles);
	boolean hasAllRoles(Subject subject, String... roles);
	
	// 身份
	void login(Subject subject, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;
	void login(Subject subject, Principal principal, HttpServletRequest request, HttpServletResponse response);
	void logout(Subject subject, HttpServletRequest request, HttpServletResponse response);
	Subject createSubject(HttpServletRequest request, HttpServletResponse response);
	void invalidate(String sessionId, String reason);
}
