package com.swak.security;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import com.swak.security.jwt.JWTPayload;

/**
 * 主体
 *
 * @author: lifeng
 * @date: 2020/3/29 21:21
 */
public interface Subject {

	/**
	 * 得到身份
	 *
	 * @return 身份
	 */
	Principal getPrincipal();

	/**
	 * 设置主体的 principal
	 *
	 * @param principal 身份
	 * @return Subject 主体
	 */
	Subject setPrincipal(Principal principal);

	/**
	 * 登录
	 *
	 * @param context 请求上下文
	 * @return 异步Token结果
	 */
	CompletionStage<Token> login(Object context);

	/**
	 * 是否有权限
	 *
	 * @param permission 权限
	 * @return 异步结果
	 */
	CompletionStage<Boolean> isPermitted(Permission permission);

	/**
	 * 是否拥有这个角色
	 *
	 * @param role 角色
	 * @return 异步结果
	 */
	CompletionStage<Boolean> hasRole(Permission role);

	/**
	 * 是否有权限
	 *
	 * @param permission 权限
	 * @return 异步结果
	 */
	CompletionStage<Boolean> isPermitted(String permission);

	/**
	 * 是否有权限
	 *
	 * @param permissions 多个权限
	 * @return 异步结果
	 */
	CompletionStage<boolean[]> isPermitted(String... permissions);

	/**
	 * 是否拥有所有的权限
	 *
	 * @param permissions 多个权限
	 * @return 异步结果
	 */
	CompletionStage<Boolean> isPermittedAll(String... permissions);

	/**
	 * 是否拥有这个角色
	 *
	 * @param role 角色
	 * @return 异步结果
	 */
	CompletionStage<Boolean> hasRole(String role);

	/**
	 * 是否拥有多个角色
	 *
	 * @param roles 多个角色
	 * @return 异步结果
	 */
	CompletionStage<boolean[]> hasRoles(String... roles);

	/**
	 * 是否拥有所有的角色
	 *
	 * @param roles 多个角色
	 * @return 异步结果
	 */
	CompletionStage<Boolean> hasAllRoles(String... roles);

	/**
	 * 设置角色
	 *
	 * @param roles 角色
	 * @return Subject
	 */
	Subject setRoles(Set<String> roles);

	/**
	 * 设置权限
	 *
	 * @param permissions 权限
	 * @return Subject
	 */
	Subject setPermissions(Set<String> permissions);

	/**
	 * 用户拥有的角色
	 *
	 * @return Subject
	 */
	Set<String> getRoles();

	/**
	 * 用户拥有的权限
	 *
	 * @return Subject
	 */
	Set<String> getPermissions();

	/**
	 * 添加附加属性 --这部分会被打包进 JWTPayload 中
	 * 
	 * @param key   名称
	 * @param value 值
	 * @return 当前对象
	 */
	Subject setAttach(String key, Object value);

	/**
	 * 获得附加属性
	 * 
	 * @param key 名称
	 * @return 附加属性
	 */
	Object getAttach(String key);

	/**
	 * 转为 Payload
	 *
	 * @return JWTPayload
	 */
	JWTPayload toPayload();

	/**
	 * 销毁 for gc
	 */
	void destory();
}