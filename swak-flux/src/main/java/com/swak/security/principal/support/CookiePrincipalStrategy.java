package com.swak.security.principal.support;

import com.swak.reactivex.Cookie;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.Session;
import com.swak.reactivex.Subject;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.principal.SessionRepository;

import reactor.core.publisher.Mono;

/**
 * 基于 COOKIE 的身份管理方式
 * @author lifeng
 */
public class CookiePrincipalStrategy implements PrincipalStrategy{

	private final String cookieName;
	private final SessionRepository sessionRepository;
	
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
		return sessionRepository.getSession(this.readCookie(request)).map(session ->{
			if (session == null) {
				this.onInvalidateSession(request, response);
			}
			if (session != null) {
				subject.setSession(session);
			}
			return subject;
		});
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
		return cookie != null ? cookie.value(): null;
	}
	public void writeCookie(HttpServerRequest request, HttpServerResponse response, String cookieValue) {
		Cookie sessionCookie = new Cookie();
		sessionCookie.name(this.cookieName);
		sessionCookie.value(cookieValue);
		sessionCookie.saveTo(request, response);
	}
	public void removeCookie(HttpServerRequest request, HttpServerResponse response) {
		Cookie sessionCookie = new Cookie();
		sessionCookie.name(this.cookieName);
		sessionCookie.removeFrom(request, response);
	}
}