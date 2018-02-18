package com.swak.security.realm;

import java.util.Set;

import com.swak.common.cache.Cache;
import com.swak.common.cache.CacheManager;
import com.swak.security.context.AuthorizationInfo;
import com.swak.security.principal.Principal;
import com.swak.security.subjct.Subject;

/**
 * 提供缓存支持
 * @author lifeng
 */
public abstract class CachedRealm implements Realm {
	
	/**
	 * 缓存的名称
	 */
	protected String cacheName;
	protected Cache cache;
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
	public AuthorizationInfo getCachedAuthorizationInfo(Principal principal) {
		String keyName = this.getCachedAuthorizationInfoName(principal);
		AuthorizationInfo value = cache.get(keyName);
		if (value == null) {
			value = this.doGetAuthorizationInfo(principal);
		}
		if (value != null) {
			cache.put(keyName, value);
		}
		return value;
	}

	/**
	 * 删除单个用户的缓存
	 */
	@Override
	public void clearCachedAuthorizationInfo(Principal principal) {
		String keyName = this.getCachedAuthorizationInfoName(principal);
		cache.delete(keyName);
	}

	/**
	 * 清空所有的缓存
	 */
	@Override
	public void clearAllCachedAuthorizationInfo() {
		cache.clear();
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
