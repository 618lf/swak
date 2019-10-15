package com.swak.vertx.security;

/**
 * 安全管理工具
 * 
 * @author lifeng
 */
public class SecurityUtils {

	public static SecurityManager securityManager = null;
	
	/**
	 * 持有 SecurityManager
	 * 
	 * @return
	 */
	public static SecurityManager getSecurityManager() {
		return securityManager;
	}
}
