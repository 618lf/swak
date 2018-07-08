package com.swak.security.web.session;

import com.swak.reactivex.transport.http.Session;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.session.support.HttpSessionManager;

/**
 * 提供session 的支持
 * 
 * @author lifeng
 */
public class SessionProvider {

	public static HttpSessionManager sessionManager;

	/**
	 * 用于获取session
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static Session getSession(HttpServerRequest request, HttpServerResponse response) {
		return request.getSubject().getSession();
	}

	/**
	 * 获取 session ， 如果 create 设置为 true ，则会创建一个session
	 * 
	 * @param create
	 * @param request
	 * @param response
	 * @return
	 */
	public static Session getSession(Boolean create, HttpServerRequest request, HttpServerResponse response) {
		Session session = SessionProvider.getSession(request, response);
		if (session == null && create) {
			session = sessionManager.createSession(request, response);
		}
		return session;
	}
}