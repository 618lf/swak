package com.swak.vertx.transport;

import java.io.Serializable;
import java.util.Set;

import com.swak.utils.Sets;

/**
 * 得到相关的权限
 *
 * @author: lifeng
 * @date: 2020/3/29 21:14
 */
public class AuthorizationInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Set<String> permissions = Sets.newHashSet();
	private Set<String> roles = Sets.newHashSet();

	public Set<String> getRoles() {
		return roles;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public AuthorizationInfo addPermission(String permission) {
		this.permissions.add(permission);
		return this;
	}

	public AuthorizationInfo addRole(String role) {
		this.roles.add(role);
		return this;
	}
}