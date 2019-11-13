package com.swak.security.permission;

import java.util.Set;

import com.swak.security.Permission;

/**
 * 无权限
 * 
 * @author lifeng
 */
public enum NonePermission implements Permission {
	INSTANCE;

	@Override
	public boolean implies(Set<String> permissions) {
		return true;
	}
}
