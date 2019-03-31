package com.swak.flux.security.context;

import java.io.Serializable;
import java.util.Set;

/**
 * 得到相关的权限
 * @author lifeng
 */
public interface AuthorizationInfo extends Serializable {

	/**
	 * 角色
	 * @return
	 */
	Set<String> getRoles();

	/**
	 * 权限
	 * @return
	 */
	Set<String> getPermissions();
}
