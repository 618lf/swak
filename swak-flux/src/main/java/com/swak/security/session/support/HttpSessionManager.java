package com.swak.security.session.support;

import com.swak.Constants;
import com.swak.reactivex.transport.http.Session;
import com.swak.reactivex.transport.http.SimpleCookie;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.session.NoneSession;
import com.swak.security.session.SessionRepository;
import com.swak.utils.StringUtils;

import io.netty.handler.codec.http.cookie.Cookie;
import reactor.core.publisher.Mono;

/**
 * Session 管理器
 * 
 * @author lifeng
 */
public class HttpSessionManager {

	private final String sessionName;
	private final SessionRepository sessionRepository;

	public HttpSessionManager(String sessionName, SessionRepository sessionRepository) {
		this.sessionName = sessionName;
		this.sessionRepository = sessionRepository;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionRepository.setSessionTimeout(sessionTimeout);
	}
	
	/**
	 * 获得 session
	 * 
	 * @param request
	 * @param response
	 */
	public Mono<Session> getSession(HttpServerRequest request, HttpServerResponse response) {
		Session session = request.getSubject().getSession();
		if (session != null) {
			return Mono.just(session);
		}
		String sessionId = this.readCookie(request);
		if (StringUtils.isNotBlank(sessionId)) {
			return sessionRepository.getSession(sessionId).map(s ->{
				if (session instanceof NoneSession) {
					this.onInvalidateSession(request, response);
				}
				return session;
			});
		}
		return Mono.just(NoneSession.NONE);
	}
	
	/**
	 * 创建session
	 */
	public Session createSession(HttpServerRequest request, HttpServerResponse response) {
		Session session = sessionRepository.createSession();
		this.writeCookie(request, response, session.getId());
		return session;
	}
	
	/**
	 * 删除session
	 */
	public Mono<Boolean> removeSession(HttpServerRequest request, HttpServerResponse response) {
		return sessionRepository.removeSession(request.getSubject().getSession()).doOnSuccess((v) -> {
			this.onInvalidateSession(request, response);
		});
	}
	
	/**
	 * 删除session
	 */
	public Mono<Boolean> removeSession(String sessionId) {
		return sessionRepository.removeSession(sessionId);
	}
	
	// 失效当前的session
	protected void onInvalidateSession(HttpServerRequest request, HttpServerResponse response) {
		this.removeCookie(request, response);
	}
	
	// cookie 相关的操作
	public String readCookie(HttpServerRequest request) {
		Cookie cookie = request.getCookie(this.sessionName);
		String sessionId = cookie != null ? cookie.value(): null;
		return Constants.deleted_cookie_value.equals(sessionId) ? null: sessionId;
	}
	public void writeCookie(HttpServerRequest request, HttpServerResponse response, String cookieValue) {
		SimpleCookie sessionCookie = new SimpleCookie(this.sessionName, cookieValue);
		sessionCookie.saveTo(request, response);
	}
	public void removeCookie(HttpServerRequest request, HttpServerResponse response) {
		SimpleCookie sessionCookie = new SimpleCookie(this.sessionName, Constants.deleted_cookie_value);
		sessionCookie.removeFrom(request, response);
	}
}