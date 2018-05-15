package com.swak.security.session;

import com.swak.reactivex.Session;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.security.subject.Subject;
import com.swak.security.utils.SecurityUtils;

/**
 * 会话数据存储方案
 * 
 * @author lifeng
 */
public class SessionProvider {

	private static SessionRepository<? extends Session> repository;

	/**
	 * 获取存储
	 * @return
	 */
	public static void setRepository(SessionRepository<? extends Session> repository) {
		SessionProvider.repository = repository;
	}

	/**
	 * 如果没有，则会创建一个session
	 * @return
	 */
	public static Session getSession(HttpServerRequest request) {
		Subject subject = SecurityUtils.getSubject(request);
		Session session = subject.getSession();
		return session != null ? session : repository.createSession(subject.getSessionId());
	}
}