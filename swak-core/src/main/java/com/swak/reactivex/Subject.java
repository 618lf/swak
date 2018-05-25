package com.swak.reactivex;

import java.util.Set;

/**
 * 简单的主体
 * @author lifeng
 */
public interface Subject {

	/**
	 * session 失效的原因
	 * @return
	 */
	String getReason();
	
	/**
	 * session 失效的原因
	 * @return
	 */
	void setReason(String reason);
	
	/**
	 * 能唯一标识一个用户登录有效的字符串
	 * cookieSession 中的sessionId
	 * token 中的 key
	 * 有 SessionId 不一定有session（或许根本不需要）
	 * @return
	 */
	String getSessionId();
	
	/**
	 * 能唯一标识一个用户登录有效的字符串
	 * cookieSession 中的sessionId
	 * token 中的 key
	 * @return
	 */
	void setSessionId(String sessionId);
	
	/**
	 * 相关Session
	 * @return
	 */
	Session getSession();
	
	/**
	 * 设置主体的session
	 * @param session
	 */
	void setSession(Session session);
	
	/**
	 * 得到身份
	 * @return
	 */
	Principal getPrincipal();
	
	/**
	 * 得到所有的身份
	 * @return
	 */
	Set<Principal> getPrincipals();
	
	/**
	 * 设置主体的principal
	 * @param session
	 */
	void setPrincipal(Principal principal);
	
	/**
	 * 是否有权限
	 * @param permission
	 * @return
	 */
	boolean isPermitted(String permission);
	
	/**
	 * 是否有权限
	 * @param permissions
	 * @return
	 */
	boolean[] isPermitted(String... permissions);
	
	/**
	 * 是否拥有所有的权限
	 * @param permissions
	 * @return
	 */
	boolean isPermittedAll(String... permissions);
	
	/**
	 * 是否拥有这个角色
	 * @param role
	 * @return
	 */
	boolean hasRole(String role);
	
	/**
	 * 是否拥有这个角色
	 * @param role
	 * @return
	 */
	boolean[] hasRoles(String... permissions);
	
	/**
	 * 是否拥有所有的角色
	 * @param permissions
	 * @return
	 */
	boolean hasAllRoles(String... permissions);
	
	/**
	 * 是否授权登录
	 * @return
	 */
	boolean isAuthenticated();
	
	/**
	 * 是否授权登录
	 * @return
	 */
	void setAuthenticated(boolean authenticated);
	
	/**
	 * 是否记住我登录
	 * @return
	 */
	boolean isRemembered();
	
	/**
	 * 以其他的身份运行
	 * @param principal
	 */
	void runAs(Principal principal);
	
	/**
	 * 登录
	 * @param token
	 */
	void login(HttpServerRequest request, HttpServerResponse response);
	
	/**
	 * 登录
	 * @param token
	 */
	void login(Principal principal, HttpServerRequest request, HttpServerResponse response);
	
	/**
	 * 退出系统
	 */
	void logout(HttpServerRequest request, HttpServerResponse response);
	
	/**
	 * 用户拥有的角色
	 * @return
	 */
	Set<String> getRoles();
	
	/**
	 * 用户拥有的权限
	 * @return
	 */
	Set<String> getPermissions();
	
	/**
	 * 设置角色
	 * @param roles
	 */
	void setRoles(Set<String> roles);

	/**
	 * 设置权限
	 * @param permissions
	 */
	void setPermissions(Set<String> permissions);
	
	/**
	 * 是否是其他身份在运行
	 * @return
	 */
	boolean isRunAs();
	
	/**
	 * 返回原始身份
	 * @return
	 */
	Object releaseRunAs();
	
	/**
	 * 销毁 for gc 
	 */
	void destory();
}