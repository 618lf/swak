package com.swak.cache;

/**
 * CacheManager管理类
 *
 * @author: lifeng
 * @date: 2020/3/29 10:08
 */
public class CacheManagers {

    /**
     * 真实的缓存管理器
     */
    private static CacheManager cacheManager;

    public static void setCacheManager(CacheManager cacheManager) {
        CacheManagers.cacheManager = cacheManager;
    }

    public static CacheManager manager() {
        return cacheManager;
    }

    /**
     * 获得一个缓存， 内部会有一个缓存来存储
     *
     * @param key 缓存名称
     * @return 缓存
     */
    public static <T> Cache<T> getCache(String key) {
        return cacheManager.getCache(key);
    }

    /**
     * 获得一个缓存， 内部会有一个缓存来存储
     *
     * @param key        缓存名称
     * @param timeToIdle 时间
     * @return 缓存
     */
    public static <T> Cache<T> getCache(String key, int timeToIdle) {
        return cacheManager.getCache(key, timeToIdle);
    }
}