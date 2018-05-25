package com.swak.eventbus.system;

/**
 * 系统事件发布
 * @author lifeng
 */
public interface SystemEventPublisher {

	/**
	 * 错误
	 * @param t
	 */
	void publishError(Throwable t);
	
	/**
	 * 登录
	 */
	void publishSignIn(Object subject);
	
	/**
	 * 注册
	 */
	void publishSignUp(Object subject);
	
	/**
	 * 退出
	 */
	void publishLogout(Object subject);
}