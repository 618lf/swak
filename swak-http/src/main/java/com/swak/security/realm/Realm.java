package com.swak.security.realm;

import com.swak.http.HttpServletRequest;
import com.swak.security.context.AuthorizationInfo;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.principal.Principal;
import com.swak.security.subjct.Subject;

/**
 * 域 -- 先这样子
 * @author lifeng
 */
public interface Realm {

	/**
	 * 直接验证，通过就返回用户信息，不通过就抛出异常
	 * 你想怎么验证就怎么验证
	 * @param token
	 * @return
	 * @throws AuthenticationException
	 */
	Principal doAuthentication(HttpServletRequest request) throws AuthenticationException;
	
	/**
	 * 获取当前身份的权限信息
	 * @param principal
	 * @return
	 */
	AuthorizationInfo getCachedAuthorizationInfo(Principal principal);
	
	/**
	 * 获取当前身份的权限信息
	 * @param principal
	 * @return
	 */
	AuthorizationInfo doGetAuthorizationInfo(Principal principal);
	
	/**
	 * 删除用户的缓存
	 * @param principal
	 */
    void clearCachedAuthorizationInfo(Principal principal);
    
    /**
     * 删除所有的缓存
     */
    void clearAllCachedAuthorizationInfo();
    
    /**
     * 登录成功
     */
    void onLoginSuccess(Subject subject, HttpServletRequest request);
    
    /**
     * 登录失败
     */
    void onLoginFailure(HttpServletRequest request);
    
    /**
     * 退出登录
     */
    void onLogout(Subject subject);
    
    /**
     * 失效
     * @param key
     * @param reason
     */
    void onInvalidate(String sessionId, String reason);
    
    /**
     * 获取原因
     * @param sessionId
     * @return
     */
    String resolveReason(String sessionId);
}