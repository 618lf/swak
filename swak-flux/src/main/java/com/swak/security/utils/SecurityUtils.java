package com.swak.security.utils;

import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.security.mgt.SecurityManager;
/**
 * 主要是获取Subject
 * 
 * @author lifeng
 */
public abstract class SecurityUtils {

	/**
	 * 系统唯一的一个在系统初始化时设置
	 */
	private static SecurityManager securityManager = null;
	
	/**
	 * 只能在 request 中获取 身份
	 * 
	 * @return
	 */
	public static Subject getSubject(HttpServerRequest request) {
		return request.getSubject();
	}

	/**
	 * 直接返回默认的管理器
	 * @return
	 */
	public static SecurityManager getSecurityManager() {
		return securityManager;
	}

	/**
	 * 设置为全局的管理器
	 * @param securityManager
	 */
	public static void setSecurityManager(SecurityManager securityManager) {
		SecurityUtils.securityManager = securityManager;
	}
}