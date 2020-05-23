package com.swak.cache;

import reactor.core.publisher.Mono;

/**
 * 响应式 cache
 *
 * @author: lifeng
 * @date: 2020/3/29 10:11
 */
public interface ReactiveCache<T> {

    /**
     * 缓存的名称
     *
     * @return 缓存的名称
     * @author lifeng
     * @date 2020/3/29 10:11
     */
    String getName();

    /**
     * 过期时间
     *
     * @param seconds 过期时间
     */
    default void setTimeToIdle(int seconds) {
    }

    /**
     * 得到默认的缓存
     *
     * @return 本地缓存
     */
    default Object getNativeCache() {
        return this;
    }

    /**
     * 得到一个值
     *
     * @param key key
     * @return 异步结果
     */
    Mono<T> getObject(String key);

    /**
     * 得到一个值
     *
     * @param key key
     * @return 异步结果
     */
    Mono<String> getString(String key);

    /**
     * 删除一个
     *
     * @param key key
     * @return 异步结果
     */
    Mono<Long> delete(String key);

    /**
     * 删除一系列值
     *
     * @param keys 多个key
     * @return 异步结果
     */
    Mono<Long> delete(String... keys);

    /**
     * key 是否存在
     *
     * @param key key
     * @return 异步结果
     */
    Mono<Long> exists(String key);

    /**
     * 添加key， 使用默认定义的时间
     *
     * @param key   key
     * @param value value
     * @return 异步结果
     */
    Mono<Entity<T>> putObject(String key, T value);

    /**
     * 添加key， 使用默认定义的时间
     *
     * @param key   key
     * @param value value
     * @return 异步结果
     */
    Mono<Entity<String>> putString(String key, String value);

    /**
     * 生存时间
     *
     * @param key key
     * @return 异步结果
     */
    Mono<Long> ttl(String key);
}
