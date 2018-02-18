package com.swak.security.principal;

public interface SessionRepository<S extends Session> {

	/**
	 * 设置存活时间
	 * @param sessionTimeout
	 */
	void setSessionTimeout(int sessionTimeout);
	
	/**
	 * 保存session
	 * @param session
	 */
	S createSession(Principal principal, boolean authenticated);
	
	/**
	 * 得到session
	 * @param id
	 * @return
	 */
	S getSession(String sessonId);
	
	/**
	 * 删除session
	 * @param id
	 */
	void removeSession(Session session);
	
	/**
	 * 失效session
	 * @param id
	 */
	void invalidateSession(String sessionId);
}
