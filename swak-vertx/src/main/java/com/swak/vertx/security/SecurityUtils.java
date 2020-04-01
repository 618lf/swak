package com.swak.vertx.security;

/**
 * 安全管理工具
 *
 * @author: lifeng
 * @date: 2020/3/29 21:10
 */
public class SecurityUtils {

    public static SecurityManager securityManager = null;

    /**
     * 持有 SecurityManager
     *
     * @return securityManager
     */
    public static SecurityManager getSecurityManager() {
        return securityManager;
    }
}
