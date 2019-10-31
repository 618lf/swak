package com.swak.security.permission;

import java.util.Set;

import com.swak.security.Permission;

/**
 * 无权限
 * 
 * @author lifeng
 */
public class NonePermission implements Permission {
	@Override
	public boolean implies(Set<String> permissions) {
		return true;
	}
}
