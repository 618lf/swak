package com.swak.security.session;

public interface SessionRepository<S extends Session> {

	/**
	 * 设置存活时间
	 * @param sessionTimeout
	 */
	void setSessionTimeout(int sessionTimeout);
	
	/**
	 * 创建一个Session, 必须指定sessionId
	 * @param session
	 */
	S createSession(String sessionId);
	
	/**
	 * 返回一个Session
	 * @param sessionId
	 * @return
	 */
	S getSession(String sessionId);
	
	/**
	 * 删除一个session
	 * @param id
	 */
	void removeSession(Session session);
}
