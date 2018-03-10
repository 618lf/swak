package com.swak.common.cache.collection;

import java.util.Map;

import com.swak.common.cache.redis.NameableCache;
import com.swak.common.cache.redis.RedisUtils;
import com.swak.common.utils.Maps;

/**
 * value 是 一个 map
 * @author lifeng
 */
public abstract class MultiMapCache<T> extends NameableCache implements MultiMap<String, T>{

	public MultiMapCache(String name) {
		this(name, -1);
	}
	
	public MultiMapCache(final String name, final int timeToIdle) {
		super(name, timeToIdle);
	}
	
	@Override
	public Map<String, T> get(String key) {
		this.expire(key);
		Map<String, byte[]> values = RedisUtils.getRedis().hGetAll(this.getKeyName(key));
		Map<String, T> maps = Maps.newHashMap();
		values.keySet().stream().forEach(s ->{
			maps.put(s, this.deserialize(values.get(s)));
		});
		return maps;
	}

	@Override
	public void put(String key, Map<String, T> v) {
		this.expire(key);
		Map<String, byte[]> tuple = Maps.newHashMap();
		v.keySet().stream().forEach(s ->{
			tuple.put(s, this.serialize(v.get(s)));
		});
		RedisUtils.getRedis().hMSet(key, tuple);
	}

	@Override
	public void delete(String key) {
		String keyName = this.getKeyName(key);
		RedisUtils.getRedis().delete(keyName);
	}

	@Override
	public T get(String key, String k2) {
		this.expire(key);
		return this.deserialize(RedisUtils.getRedis().hGet(this.getKeyName(key), k2));
	}

	@Override
	public void pub(String key, String k2, T v) {
		this.expire(key);
		RedisUtils.getRedis().hSet(this.getKeyName(key), k2, this.serialize(v));
	}

	@Override
	public void delete(String key, String k2) {
		this.expire(key);
		RedisUtils.getRedis().hDel(this.getKeyName(key), k2);
	}
	
	/**
	 * 序例化的方式
	 * @param t
	 * @return
	 */
	protected abstract byte[] serialize(T t);
	
	/**
	 * 序例化的方式
	 * @param t
	 * @return
	 */
	protected abstract T deserialize(byte[] bytes);
}