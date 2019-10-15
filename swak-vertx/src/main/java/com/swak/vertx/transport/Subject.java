package com.swak.vertx.transport;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import com.swak.security.jwt.JWTPayload;

/**
 * 简单的主体
 * 
 * @author lifeng
 */
public interface Subject {

	/**
	 * 得到身份
	 * 
	 * @return
	 */
	Principal getPrincipal();

	/**
	 * 设置主体的principal
	 * 
	 * @param session
	 */
	Subject setPrincipal(Principal principal);

	/**
	 * 是否有权限
	 * 
	 * @param permission
	 * @return
	 */
	CompletionStage<Boolean> isPermitted(String permission);

	/**
	 * 是否有权限
	 * 
	 * @param permissions
	 * @return
	 */
	CompletionStage<boolean[]> isPermitted(String... permissions);

	/**
	 * 是否拥有所有的权限
	 * 
	 * @param permissions
	 * @return
	 */
	CompletionStage<Boolean> isPermittedAll(String... permissions);

	/**
	 * 是否拥有这个角色
	 * 
	 * @param role
	 * @return
	 */
	CompletionStage<Boolean> hasRole(String role);

	/**
	 * 是否拥有这个角色
	 * 
	 * @param role
	 * @return
	 */
	CompletionStage<boolean[]> hasRoles(String... permissions);

	/**
	 * 是否拥有所有的角色
	 * 
	 * @param permissions
	 * @return
	 */
	CompletionStage<Boolean> hasAllRoles(String... permissions);

	/**
	 * 设置角色
	 * 
	 * @param roles
	 */
	Subject setRoles(Set<String> roles);

	/**
	 * 设置权限
	 * 
	 * @param permissions
	 */
	Subject setPermissions(Set<String> permissions);
	
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
	 * 转为 Payload
	 * 
	 * @return
	 */
	JWTPayload toPayload();

	/**
	 * 销毁 for gc
	 */
	void destory();
}