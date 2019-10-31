package com.swak.security;

import java.util.Set;

import com.swak.security.permission.NonePermission;

/**
 * 权限标示，将 @Auth 和 路径权限配置封装为 Permission
 * 
 * @author lifeng
 */
public interface Permission {
	
	/**
	 * 无权限
	 */
	NonePermission NONE = new NonePermission();

	/**
	 * 是否符合这些权限
	 * 
	 * @param p
	 * @return
	 */
	boolean implies(Set<String> permissions);
}
