package com.swak.vertx.transport;

import java.io.Serializable;
import java.util.Set;

import com.swak.utils.Sets;

/**
 * 得到相关的权限
 * 
 * @author lifeng
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

	public void addPermission(String permission) {
		this.permissions.add(permission);
	}

	public void addRole(String role) {
		this.roles.add(role);
	}
}