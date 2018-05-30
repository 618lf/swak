package com.swak.security.principal.support;

import com.swak.reactivex.Cookie;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.Session;
import com.swak.reactivex.Subject;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.session.SessionRepository;
import com.swak.utils.StringUtils;

/**
 * 基于 COOKIE 的身份管理方式
 * @author lifeng
 */
public class CookiePrincipalStrategy implements PrincipalStrategy{

	private String cookieName = "SESSION";
	private SessionRepository<? extends Session> sessionRepository;
	
	/**
	 * 设置 session 存储
	 * @param sessionRepository
	 */
	public void setSessionRepository(SessionRepository<? extends Session> sessionRepository) {
		this.sessionRepository = sessionRepository;
	}
	
	/**
	 * 登录时用于创建身份
	 */
	@Override
	public void createPrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		Session session = sessionRepository.createSession(subject.getPrincipal(), subject.isAuthenticated());
		subject.setSession(session);
		this.onNewSession(session, request, response);
	}

	/**
	 * 身份已经失效
	 */
	@Override
	public void invalidatePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		sessionRepository.removeSession(subject.getSession());
		this.onInvalidateSession(request, response);
	}

	/**
	 * 获取身份
	 */
	@Override
	public void resolvePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		// 获取sesson
		Session session = null;
		String sessionId = this.readCookie(request);
		if (StringUtils.hasText(sessionId)) {
			session = sessionRepository.getSession(sessionId);
			if (session == null) {
				this.onInvalidateSession(request, response);
			}
			
			// 保存 sessionId
			subject.setSessionId(sessionId);
		}
		
		// 保存 session 信息
		if (session != null) {
			subject.setSession(session);
		}
	}

	/**
	 * 将身份失效
	 */
	@Override
	public void invalidatePrincipal(String sessionId) {
		sessionRepository.removeSession(sessionId);
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