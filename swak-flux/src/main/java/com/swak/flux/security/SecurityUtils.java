package com.swak.flux.security;

import com.swak.flux.security.mgt.SecurityManager;
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