package com.swak.security.session;

import com.swak.reactivex.Principal;
import com.swak.reactivex.Session;

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
	 * 创建一个Session， 指定身份
	 * @param session
	 */
	S createSession(Principal principal, boolean authenticated);
	
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
	
	/**
	 * 删除一个session
	 * @param sessionId
	 */
	void removeSession(String sessionId);
}
