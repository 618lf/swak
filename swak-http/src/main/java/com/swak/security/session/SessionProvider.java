package com.swak.security.session;

import com.swak.common.utils.SpringContextHolder;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.security.subject.Subject;
import com.swak.security.utils.SecurityUtils;

/**
 * 会话数据存储方案
 * 
 * @author lifeng
 */
public class SessionProvider {

	private static SessionRepository<Session> repository;

	/**
	 * 获取存储
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static SessionRepository<Session> getRepository() {
		if (repository == null) {
			repository = SpringContextHolder.getBeanQuietly(SessionRepository.class);
		}
		return repository;
	}

	/**
	 * 如果没有，则会创建一个session
	 * @return
	 */
	public static Session getSession(HttpServerRequest request) {
		Subject subject = SecurityUtils.getSubject(request);
		Session session = subject.getSession();
		return session != null ? session : getRepository().createSession(subject.getSessionId());
	}
}