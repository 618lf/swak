package com.swak.cache;

/**
 * 声明本地缓存
 *
 * @author: lifeng
 * @date: 2020/3/29 10:10
 */
public interface LocalCache<T> extends Cache<T> {

    /**
     * 发送删除Key的命令
     *
     * @param key 命令
     */
    void sendEvictCmd(Object key);
}
