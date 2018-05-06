package com.swak.security.realm;

import java.util.Set;

import com.swak.common.cache.Cache;
import com.swak.common.cache.CacheManager;
import com.swak.security.context.AuthorizationInfo;
import com.swak.security.principal.Principal;
import com.swak.security.subject.Subject;

/**
 * 提供缓存的支持
 * @author lifeng
 */
public abstract class CachedRealm implements Realm {

	/**
	 * 缓存的名称
	 */
	protected String cacheName;
	protected Cache<AuthorizationInfo> cache;
	protected CacheManager cacheManager;
	
	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
		cache = this.cacheManager.getCache(this.getCacheName());
	}

	/**
	 * 优先获取缓存中的数据
	 */
	@Override
	public AuthorizationInfo doGetAuthorizationInfo(Principal principal) {
		String keyName = this.getCachedAuthorizationInfoName(principal);
		AuthorizationInfo value = cache.getObject(keyName);
		if (value == null) {
			value = this.getAuthorizationInfo(principal);
		}
		if (value != null) {
			cache.putObject(keyName, value);
		}
		return value;
	}
	
	/**
	 * 获取权限信息
	 * @param principal
	 * @return 
	 */
	protected abstract AuthorizationInfo getAuthorizationInfo(Principal principal);

	/**
	 * 删除单个用户的缓存
	 */
	public void clearCachedAuthorizationInfo(Principal principal) {
		String keyName = this.getCachedAuthorizationInfoName(principal);
		cache.delete(keyName);
	}

	/**
	 * 获得缓存名称
	 * @param principal
	 * @return
	 */
	protected String getCachedAuthorizationInfoName(Principal principal) {
		return principal.toString();
	}

	/**
	 * 退出清空缓存
	 */
	@Override
	public void onLogout(Subject subject) {
		Set<Principal> principals = subject.getPrincipals();
		for(Principal principal: principals) {
			this.clearCachedAuthorizationInfo(principal);
		}
	}
}
