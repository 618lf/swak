package com.swak.security.mgt;

import com.swak.reactivex.Principal;
import com.swak.reactivex.Subject;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.security.exception.AuthenticationException;

/**
 * 总体的控制器
 * 
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
	void login(Subject subject, HttpServerRequest request, HttpServerResponse response) throws AuthenticationException;

	void login(Subject subject, Principal principal, HttpServerRequest request, HttpServerResponse response);

	void logout(Subject subject, HttpServerRequest request, HttpServerResponse response);

	Subject createSubject(HttpServerRequest request, HttpServerResponse response);

	void invalidate(String sessionId, String reason);
}
