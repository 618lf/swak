package com.swak.vertx.security.realm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.cache.AsyncCache;
import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.vertx.transport.AuthorizationInfo;
import com.swak.vertx.transport.Principal;
import com.swak.vertx.transport.Subject;

/**
 * 提供缓存的支持
 * 
 * @author lifeng
 */
public abstract class CachedRealm implements Realm {

	/**
	 * 缓存的名称
	 */
	protected String cacheName;
	protected int timeOut = 1800;
	protected AsyncCache<AuthorizationInfo> cache;
	protected CacheManager cacheManager;

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
		Cache<AuthorizationInfo> cache = this.cacheManager.getCache(this.getCacheName(), this.getTimeOut());
		this.cache = cache.async();
	}

	/**
	 * 优先获取缓存中的数据
	 */
	@Override
	public CompletionStage<AuthorizationInfo> doGetAuthorizationInfo(Subject subject) {
		String keyName = this.getCachedAuthorizationInfoName(subject.getPrincipal());
		return cache.getObject(keyName).thenCompose(value -> {
			if (value == null) {
				return this.getAuthorizationInfo(subject.getPrincipal())
						.thenCompose(authorization -> cache.putObject(keyName, authorization))
						.thenApply(entity -> entity.getValue());
			}
			return CompletableFuture.completedFuture(value);
		});
	}

	/**
	 * 获取权限信息
	 * 
	 * @param principal
	 * @return
	 */
	protected abstract CompletionStage<AuthorizationInfo> getAuthorizationInfo(Principal principal);

	/**
	 * 删除单个用户的缓存
	 */
	public void clearCachedAuthorizationInfo(Principal principal) {
		String keyName = this.getCachedAuthorizationInfoName(principal);
		cache.delete(keyName);
	}

	/**
	 * 获得缓存名称
	 * 
	 * @param principal
	 * @return
	 */
	protected String getCachedAuthorizationInfoName(Principal principal) {
		return principal.toString();
	}
}