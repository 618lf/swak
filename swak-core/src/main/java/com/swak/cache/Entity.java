package com.swak.cache;

/**
 * 缓存的实体
 * 
 * @author lifeng
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