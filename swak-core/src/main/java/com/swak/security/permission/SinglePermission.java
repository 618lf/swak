package com.swak.security.permission;

import java.util.Set;

import com.swak.security.Permission;

/**
 * 单个权限,只验证包含即可
 * 
 * @author lifeng
 */
public class SinglePermission implements Permission {

	private String permission;

	public SinglePermission(String permission) {
		this.permission = permission;
	}

	@Override
	public boolean implies(Set<String> permissions) {
		return permissions.contains(permission);
	}
}