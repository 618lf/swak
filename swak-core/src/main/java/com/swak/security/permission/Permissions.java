package com.swak.security.permission;

import java.util.List;
import java.util.Set;

import com.swak.security.Permission;
import com.swak.utils.Lists;

/**
 * 多级别的验证
 * 
 * @author lifeng
 */
public class Permissions implements Permission{

	private List<Permission>  permissions = Lists.newArrayList();
	
	public Permissions addPermission(Permission permission) {
		permissions.add(permission);
		return this;
	}
	
	@Override
	public boolean implies(Set<String> permissions) {
		for (Permission permission: this.permissions) {
			if(!permission.implies(permissions)) {
				return false;
			}
		}
		return true;
	}
}
