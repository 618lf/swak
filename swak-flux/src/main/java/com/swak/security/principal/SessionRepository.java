package com.swak.security.principal;

import com.swak.reactivex.Principal;
import com.swak.reactivex.Session;

import reactor.core.publisher.Mono;

public interface SessionRepository {

	/**
	 * 设置存活时间
	 * @param sessionTimeout
	 */
	void setSessionTimeout(int sessionTimeout);
	
	/**
	 * 创建一个Session， 指定身份
	 * @param session
	 */
	Mono<Session> createSession(Principal principal, boolean authenticated);
	
	/**
	 * 返回一个Session
	 * @param sessionId
	 * @return
	 */
	Mono<Session> getSession(String sessionId);
	
	/**
	 * 删除一个session
	 * @param id
	 */
	Mono<Boolean> removeSession(Session session);
	
	/**
	 * 删除一个session
	 * @param sessionId
	 */
	Mono<Boolean> removeSession(String sessionId);
}