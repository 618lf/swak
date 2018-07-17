package com.swak.security.session;

import java.util.concurrent.CompletionStage;

import com.swak.reactivex.transport.http.Session;

public interface SessionRepository {

	/**
	 * 设置存活时间
	 * @param sessionTimeout
	 */
	void setSessionTimeout(int sessionTimeout);
	
	/**
	 * 创建一个Session
	 * @param request
	 * @param response
	 * @return
	 */
	Session createSession();
	
	/**
	 * 返回一个Session
	 * @param sessionId
	 * @return
	 */
	CompletionStage<Session> getSession(String sessionId);
	
	/**
	 * 删除一个session
	 * @param id
	 */
	CompletionStage<Boolean> removeSession(Session session);
	
	/**
	 * 删除一个session
	 * @param sessionId
	 */
	CompletionStage<Boolean> removeSession(String sessionId);
}