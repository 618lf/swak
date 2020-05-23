package com.swak.cache;

/**
 * 基本的缓存操作
 *
 * @author: lifeng
 * @date: 2020/3/29 10:03
 */
public interface Cache<T> {

    /**
     * 缓存的名称
     *
     * @return 缓存的名称
     * @author lifeng
     * @date 2020/3/29 10:05
     */
    String getName();

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
     * @return 结果
     */
    T getObject(String key);

    /**
     * 得到一个值
     *
     * @param key key
     * @return 结果
     */
    String getString(String key);

    /**
     * 删除一个
     *
     * @param key key
     * @return 结果
     */
    Long delete(String key);

    /**
     * 删除一系列值
     *
     * @param keys 多个key
     * @return 结果
     */
    Long delete(String... keys);

    /**
     * key 是否存在
     *
     * @param key key
     * @return 结果
     */
    Boolean exists(String key);

    /**
     * 添加key， 使用默认定义的时间
     *
     * @param key   key
     * @param value value
     * @return 结果
     */
    Entity<T> putObject(String key, T value);

    /**
     * 添加key， 使用默认定义的时间
     *
     * @param key   key
     * @param value value
     * @return 结果
     */
    Entity<String> putString(String key, String value);

    /**
     * 生存时间
     *
     * @param key key
     * @return 结果
     */
    Long ttl(String key);

    /**
     * 转为异步缓存
     *
     * @return 包裹为异步缓存
     */
    default AsyncCache<T> async() {
        return null;
    }

    /**
     * 转为响应式缓存
     *
     * @return 包裹为响应式缓存
     */
    default ReactiveCache<T> reactive() {
        return null;
    }

    /**
     * 转为二级缓存
     *
     * @return 转为二级缓存
     */
    default Cache<T> wrapLocal() {
        return null;
    }
}