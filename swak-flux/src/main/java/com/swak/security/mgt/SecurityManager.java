package com.swak.security.mgt;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;
import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Subject;

import reactor.core.publisher.Mono;

/**
 * 总体的控制器
 * 
 * @author lifeng
 */
public interface SecurityManager {

	// 权限
	Mono<Boolean> isPermitted(Subject subject, String permission);
	Mono<boolean[]> isPermitted(Subject subject, String... permissions);
	Mono<Boolean> isPermittedAll(Subject subject, String... permissions);
	Mono<Boolean> hasRole(Subject subject, String role);
	Mono<boolean[]> hasRoles(Subject subject, String... roles);
	Mono<Boolean> hasAllRoles(Subject subject, String... roles);

	// 身份
	Mono<Boolean> login(Subject subject, HttpServerRequest request, HttpServerResponse response);
	Mono<Boolean> login(Subject subject, Principal principal, HttpServerRequest request, HttpServerResponse response);
	Mono<Boolean> logout(Subject subject, HttpServerRequest request, HttpServerResponse response);
	Mono<Subject> createSubject(HttpServerRequest request, HttpServerResponse response);
	Mono<Boolean> invalidate(String sessionId, String reason);
}
