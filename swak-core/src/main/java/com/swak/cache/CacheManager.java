package com.swak.cache;

/**
 * 缓存管理器
 *
 * @author: lifeng
 * @date: 2020/3/29 10:06
 */
public interface CacheManager {

    /**
     * 通过名称获得一个缓存
     *
     * @param name 缓存名称
     * @return 缓存
     */
    <T> Cache<T> getCache(String name);

    /**
     * 创建一个缓存
     *
     * @param name       缓存名称
     * @param timeToIdle idle时间
     * @return 缓存
     */
    <T> Cache<T> getCache(String name, int timeToIdle);

    /**
     * 创建一个缓存
     *
     * @param name       缓存名称
     * @param timeToIdle 固定存活时间
     * @param idleAble   idle时间
     * @return 缓存
     */
    <T> Cache<T> getCache(String name, int timeToIdle, boolean idleAble);

    /**
     * 获得本地缓存
     *
     * @return 二级缓存
     */
    LocalCache<Object> getLocalCache();
}
