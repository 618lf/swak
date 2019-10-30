package com.swak.security.permission;

import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.swak.security.Permission;

/**
 * or 权限
 * 
 * @author lifeng
 */
public class OrPermission implements Permission {

	private String[] permissions;

	private OrPermission(String[] permissions) {
		this.permissions = permissions;
	}

	@Override
	public boolean implies(Set<String> permissions) {
		if (CollectionUtils.isEmpty(permissions)) {
			return false;
		}
		if (this.permissions != null && this.permissions.length != 0) {
			for (String p : this.permissions) {
				if (permissions.contains(p)) {
					return true;
				}
			}
		}
		return false;
	}
}
