package com.swak.cache;

/**
 * 缓存的实体
 *
 * @author: lifeng
 * @date: 2020/3/29 10:10
 */
public class Entity<T> {

    private String key;
    private T value;

    public Entity(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }
}