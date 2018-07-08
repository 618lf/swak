package com.swak.security.principal.support;

import com.swak.reactivex.transport.http.Session;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.session.NoneSession;
import com.swak.security.session.support.HttpSessionManager;

import reactor.core.publisher.Mono;

/**
 * 基于 COOKIE 的身份管理方式
 * 
 * Redis 操作是否改为同步的是否更好
 * 
 * @author lifeng
 */
public class SessionPrincipalStrategy implements PrincipalStrategy {

	private final HttpSessionManager sessionManager;

	public SessionPrincipalStrategy(HttpSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	/**
	 * 获取身份
	 */
	@Override
	public Mono<Subject> resolvePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		return sessionManager.getSession(false, request, response).map(session -> {
			if (!(session instanceof NoneSession)) {
				subject.setSession(session);
			}
			return subject;
		});
	}

	/**
	 * 登录时用于创建身份
	 */
	@Override
	public Mono<Subject> createPrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		Session session = sessionManager.createSession(request, response);
		session.setPrincipal(subject.getPrincipal());
		session.setAuthenticated(subject.isAuthenticated());
		subject.setSession(session);
		return Mono.just(subject);
	}

	/**
	 * 身份已经失效
	 */
	@Override
	public Mono<Boolean> invalidatePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		return sessionManager.removeSession(request, response);
	}

	/**
	 * 将身份失效
	 */
	@Override
	public Mono<Boolean> invalidatePrincipal(String sessionId) {
		return sessionManager.removeSession(sessionId);
	}
}