package com.swak.security.mgt;

import java.util.concurrent.CompletionStage;

import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 总体的控制器
 * 
 * @author lifeng
 */
public interface SecurityManager {

	// 权限
	CompletionStage<Boolean> isPermitted(Subject subject, String permission);
	CompletionStage<boolean[]> isPermitted(Subject subject, String... permissions);
	CompletionStage<Boolean> isPermittedAll(Subject subject, String... permissions);
	CompletionStage<Boolean> hasRole(Subject subject, String role);
	CompletionStage<boolean[]> hasRoles(Subject subject, String... roles);
	CompletionStage<Boolean> hasAllRoles(Subject subject, String... roles);

	// 身份
	Mono<Boolean> login(Subject subject, HttpServerRequest request, HttpServerResponse response);
	Mono<Boolean> login(Subject subject, Principal principal, HttpServerRequest request, HttpServerResponse response);
	Mono<Boolean> logout(Subject subject, HttpServerRequest request, HttpServerResponse response);
	Mono<Subject> createSubject(HttpServerRequest request, HttpServerResponse response);
}
