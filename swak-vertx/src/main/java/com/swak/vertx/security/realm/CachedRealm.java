package com.swak.vertx.security.realm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.cache.AsyncCache;
import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.cache.Entity;
import com.swak.security.AuthorizationInfo;
import com.swak.security.Principal;
import com.swak.security.Subject;

/**
 * 提供缓存的支持
 *
 * @author: lifeng
 * @date: 2020/3/29 20:44
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
                        .thenApply(Entity::getValue);
            }
            return CompletableFuture.completedFuture(value);
        });
    }

    /**
     * 获取权限信息
     *
     * @param principal 身份信息
     * @return 异步权限信息
     */
    protected abstract CompletionStage<AuthorizationInfo> getAuthorizationInfo(Principal principal);

	/**
	 * 删除单个用户的缓存
	 *
	 * @param principal 身份信息
	 */
	public void clearCachedAuthorizationInfo(Principal principal) {
        String keyName = this.getCachedAuthorizationInfoName(principal);
        cache.delete(keyName);
    }

    /**
     * 获得缓存名称
     *
     * @param principal 身份信息
     * @return 缓存名称
     */
    protected String getCachedAuthorizationInfoName(Principal principal) {
        return principal.toString();
    }
}