package com.swak.flux.security.realm;

import java.util.concurrent.CompletionStage;

import com.swak.flux.security.context.AuthorizationInfo;
import com.swak.flux.transport.Principal;
import com.swak.flux.transport.Subject;
import com.swak.flux.transport.server.HttpServerRequest;

public interface Realm {

	/**
	 * 直接验证，通过就返回用户信息，不通过就抛出异常
	 * 你想怎么验证就怎么验证
	 * @param token
	 * @return
	 */
	CompletionStage<Principal> doAuthentication(HttpServerRequest request);
	
	/**
	 * 获取当前身份的权限信息
	 * @param principal
	 * @return
	 */
	CompletionStage<AuthorizationInfo> doGetAuthorizationInfo(Principal principal);
	
    /**
     * 登录成功 (如果是耗时的操作，请异步完成)
     */
    void onLoginSuccess(Subject subject, HttpServerRequest request);
    
    /**
     * 登录失败 (如果是耗时的操作，请异步完成)
     */
    void onLoginFailure(HttpServerRequest request);
    
    /**
     * 退出登录 (如果是耗时的操作，请异步完成)
     */
    void onLogout(Subject subject);
    
    /**
     * 失效 (如果是耗时的操作，请异步完成)
     * @param key
     * @param reason
     */
    void onInvalidate(String sessionId, String reason);
}