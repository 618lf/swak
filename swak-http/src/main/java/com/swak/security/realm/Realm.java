package com.swak.security.realm;

import com.swak.reactivex.Principal;
import com.swak.reactivex.Subject;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.security.context.AuthorizationInfo;
import com.swak.security.exception.AuthenticationException;

public interface Realm {

	/**
	 * 直接验证，通过就返回用户信息，不通过就抛出异常
	 * 你想怎么验证就怎么验证
	 * @param token
	 * @return
	 * @throws AuthenticationException
	 */
	Principal doAuthentication(HttpServerRequest request) throws AuthenticationException;
	
	/**
	 * 获取当前身份的权限信息
	 * @param principal
	 * @return
	 */
	AuthorizationInfo doGetAuthorizationInfo(Principal principal);
	
    /**
     * 登录成功
     */
    void onLoginSuccess(Subject subject, HttpServerRequest request);
    
    /**
     * 登录失败
     */
    void onLoginFailure(HttpServerRequest request);
    
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