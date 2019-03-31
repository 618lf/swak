package com.swak.flux.transport.http;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 简单的主体
 * @author lifeng
 */
public interface Subject {

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
	CompletionStage<Boolean> isPermitted(String permission);
	
	/**
	 * 是否有权限
	 * @param permissions
	 * @return
	 */
	CompletionStage<boolean[]> isPermitted(String... permissions);
	
	/**
	 * 是否拥有所有的权限
	 * @param permissions
	 * @return
	 */
	CompletionStage<Boolean> isPermittedAll(String... permissions);
	
	/**
	 * 是否拥有这个角色
	 * @param role
	 * @return
	 */
	CompletionStage<Boolean> hasRole(String role);
	
	/**
	 * 是否拥有这个角色
	 * @param role
	 * @return
	 */
	CompletionStage<boolean[]> hasRoles(String... permissions);
	
	/**
	 * 是否拥有所有的角色
	 * @param permissions
	 * @return
	 */
	CompletionStage<Boolean> hasAllRoles(String... permissions);
	
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
	CompletionStage<Boolean> runAs(Principal principal);
	
	/**
	 * 是否是其他身份在运行
	 * @return
	 */
	boolean isRunAs();
	
	/**
	 * 返回原始身份
	 * @return
	 */
	CompletionStage<Principal> releaseRunAs();
	
	/**
	 * 登录
	 * @param token
	 */
	Mono<Boolean> login(HttpServerRequest request, HttpServerResponse response);
	
	/**
	 * 登录
	 * @param token
	 */
	Mono<Boolean> login(Principal principal, HttpServerRequest request, HttpServerResponse response);
	
	/**
	 * 退出系统
	 */
	Mono<Boolean> logout(HttpServerRequest request, HttpServerResponse response);
	
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
	 * 销毁 for gc 
	 */
	void destory();
}