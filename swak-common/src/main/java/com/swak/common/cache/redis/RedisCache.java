package com.swak.common.cache.redis;

import com.swak.common.cache.Cache;
import com.swak.common.serializer.SerializationUtils;

import redis.clients.util.SafeEncoder;

/**
 * Redis 需要在配置文件中配置,以后在添加详细的参数
 * 
 * 如果有过期时间则每次查询会通过lua来执行，有20%的性能提升
 * @author lifeng
 */
public class RedisCache<T> extends NameableCache implements Cache<T> {

	public static String GET_LUA = null;
	public static String EXISTS_LUA = null;
	
	static {
		GET_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[2]); return redis.call(\"GET\", KEYS[1]);").toString();
		EXISTS_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[2]); return redis.call(\"EXISTS\", KEYS[1]);").toString();
	}
	
	/**
	 * 默认不过期
	 * @param name
	 */
	public RedisCache(String name) {
		super(name);
	}
	
	/**
	 * 指定过期时间
	 * @param name
	 * @param timeToIdle
	 */
	public RedisCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T getObject(String key) {
		if (!isValid()) {
			return (T) SerializationUtils.deserialize(this._get(key));
		}
		return (T) SerializationUtils.deserialize(this._hget(key));
	}
	
	@Override
	public String getString(String key) {
		if (!isValid()) {
			return SafeEncoder.encode(this._get(key));
		}
		return SafeEncoder.encode(this._hget(key));
	}
	
	@Override
	public boolean exists(String key) {
		if (!isValid()) {
			return _exists(key);
		}
		return _hexists(key);
	}

	@Override
	public void delete(String key) {
		this._del(key);
	}

	@Override
	public void delete(String ... keys) {
		this._del(keys);
	}

	@Override
	public void putObject(String key, T value) {
		this._set(key, SerializationUtils.serialize(value));
	}
	
	@Override
	public void putString(String key, String value) {
		this._set(key, SafeEncoder.encode(value));
	}

	@Override
	public void clear() {
		String _key = new StringBuilder(name).append(prex).append("*").toString();
		RedisUtils.getRedis().deletes(_key);
	}

	@Override
	public long ttl(String key) {
		return RedisUtils.getRedis().ttl(getKeyName(key));
	}

	/**
	 * 原生的获取
	 * 
	 * @param key
	 * @return
	 */
	protected byte[] _get(String key) {
		String keyName = this.getKeyName(key);
		return RedisUtils.getRedis().get(keyName);
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected byte[] _hget(String key) {
		String script = GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
	    return (byte[])RedisUtils.getRedis().runAndGetOne(script, values);
	}
	
	/**
	 * 原生的设置
	 * 
	 * @param key
	 * @param value
	 * @param expiration
	 */
	protected void _set(String key, byte[] value) {
		String keyName = this.getKeyName(key);
		if (isValid()) {
			RedisUtils.getRedis().set(keyName, value, this.timeToIdle);
		} else {
			RedisUtils.getRedis().set(keyName, value);
		}
	}
	
	/**
	 * 删除当前的key
	 */
	protected void _del(String... keys) {
		RedisUtils.getRedis().delete(keys);
	}

	/**
	 * 当前key 是否存在
	 */
	protected boolean _exists(String key) {
		return RedisUtils.getRedis().exists(key);
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected boolean _hexists(String key) {
		String script = EXISTS_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
	    Long e = RedisUtils.getRedis().runAndGetOne(script, values);
		return e != null && e == 1;
	}
}
