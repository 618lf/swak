package com.swak.security.principal.support;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;
import com.swak.reactivex.transport.http.Session;
import com.swak.reactivex.transport.http.SimpleCookie;
import com.swak.reactivex.transport.http.Subject;
import com.swak.security.principal.NoneSession;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.principal.SessionRepository;
import com.swak.utils.StringUtils;

import io.netty.handler.codec.http.cookie.Cookie;
import reactor.core.publisher.Mono;

/**
 * 基于 COOKIE 的身份管理方式
 * @author lifeng
 */
public class CookiePrincipalStrategy implements PrincipalStrategy{

	private final String cookieName;
	private final SessionRepository sessionRepository;
	private final String DELETED_COOKIE_VALUE = "deleteMe";
	
	public CookiePrincipalStrategy(String cookieName, SessionRepository sessionRepository) {
		this.cookieName = cookieName;
		this.sessionRepository = sessionRepository;
	}

	/**
	 * 登录时用于创建身份
	 */
	@Override
	public Mono<Subject> createPrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		return sessionRepository.createSession(subject.getPrincipal(), subject.isAuthenticated()).map(session ->{
			subject.setSession(session);
			this.onNewSession(session, request, response);
			return subject;
		});
	}

	/**
	 * 身份已经失效
	 */
	@Override
	public Mono<Boolean> invalidatePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		return sessionRepository.removeSession(subject.getSession()).doOnSuccess((v) ->{
			this.onInvalidateSession(request, response);
		});
	}

	/**
	 * 获取身份
	 */
	@Override
	public Mono<Subject> resolvePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		String sessionId = this.readCookie(request);
		if (StringUtils.hasText(sessionId)) {
			return sessionRepository.getSession(this.readCookie(request)).map(session ->{
				if (session instanceof NoneSession) {
					this.onInvalidateSession(request, response);
				} else {
					subject.setSession(session);
				}
				return subject;
			});
		}
		return Mono.just(subject);
	}

	/**
	 * 将身份失效
	 */
	@Override
	public Mono<Boolean> invalidatePrincipal(String sessionId) {
		return sessionRepository.removeSession(sessionId);
	}
	
	protected void onNewSession(Session session, HttpServerRequest request, HttpServerResponse response) {
		this.writeCookie(request, response, session.getId());
	}
	protected void onInvalidateSession(HttpServerRequest request, HttpServerResponse response) {
		this.removeCookie(request, response);
	}
	
	// cookie 相关的操作
	public String readCookie(HttpServerRequest request) {
		Cookie cookie = request.getCookie(this.cookieName);
		String sessionId = cookie != null ? cookie.value(): null;
		return DELETED_COOKIE_VALUE.equals(sessionId) ? null: sessionId;
	}
	public void writeCookie(HttpServerRequest request, HttpServerResponse response, String cookieValue) {
		SimpleCookie sessionCookie = new SimpleCookie(this.cookieName, cookieValue);
		sessionCookie.saveTo(request, response);
	}
	public void removeCookie(HttpServerRequest request, HttpServerResponse response) {
		SimpleCookie sessionCookie = new SimpleCookie(this.cookieName, DELETED_COOKIE_VALUE);
		sessionCookie.removeFrom(request, response);
	}
}