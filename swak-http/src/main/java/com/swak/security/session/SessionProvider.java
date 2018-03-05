package com.swak.security.session;

import com.swak.common.utils.SpringContextHolder;
import com.swak.security.subjct.Subject;
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
	public static Session getSession() {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		return session != null ? session : getRepository().createSession(subject.getSessionId());
	}

	/**
	 * 调用 自定义的 session
	 * 
	 * @param key
	 * @param value
	 */
	public static void setAttribute(String key, Object value) {
		Session session = getSession();
		if (session != null) {
			session.setAttribute(key, value);
		}
	}

	/**
	 * 调用 自定义的 session
	 * 
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(String key) {
		Session session = getSession();
		if (session != null) {
			return (T) (session.getAttribute(key));
		}
		return null;
	}

	/**
	 * 删除会话属性
	 * 
	 * @param key
	 */
	public static void removeAttribute(String key) {
		Session session = getSession();
		if (session != null) {
			session.removeAttribute(key);
		}
	}
}