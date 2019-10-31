package com.swak.security.permission;

import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.swak.security.Permission;

/**
 * 多个权限
 * 
 * @author lifeng
 */
public class AndPermission implements Permission {

	private String[] permissions;

	public AndPermission(String[] permissions) {
		this.permissions = permissions;
	}

	@Override
	public boolean implies(Set<String> permissions) {
		if (CollectionUtils.isEmpty(permissions)) {
			return false;
		}
		if (this.permissions != null && this.permissions.length != 0) {
			for (String p : this.permissions) {
				if (!permissions.contains(p)) {
					return false;
				}
			}
		}
		return true;
	}
}
