package com.swak.cache.redis;

import java.util.List;

import com.swak.cache.Cache;
import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.serializer.SerializationUtils;
import com.swak.utils.Lists;

/**
 * Redis 需要在配置文件中配置,以后在添加详细的参数
 * 
 * 如果有过期时间则每次查询会通过lua来执行，有20%的性能提升
 * @author lifeng
 */
public class RedisCache<T> extends NameableCache implements Cache<T> {

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
	public long ttl(String key) {
		return RedisUtils.ttl(getKeyName(key));
	}

	/**
	 * 原生的获取
	 * 
	 * @param key
	 * @return
	 */
	protected byte[] _get(String key) {
		String keyName = this.getKeyName(key);
		return RedisUtils.get(keyName);
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected byte[] _hget(String key) {
		String script = Cons.GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return RedisUtils.runScript(script, values);
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
			RedisUtils.set(keyName, value, this.timeToIdle);
		} else {
			RedisUtils.set(keyName, value);
		}
	}
	
	/**
	 * 删除当前的key
	 */
	protected void _del(String... keys) {
		if (keys.length == 1) {
			RedisUtils.del(this.getKeyName(keys[0]));
		} else {
			List<String> _keys = Lists.newArrayList(keys.length);
			for(String key: keys) {
				_keys.add(this.getKeyName(key));
			}
			RedisUtils.del(_keys.toArray(keys));
		}
	}

	/**
	 * 当前key 是否存在
	 */
	protected boolean _exists(String key) {
		return RedisUtils.exists(this.getKeyName(key));
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected boolean _hexists(String key) {
		String script = Cons.EXISTS_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		Long e = RedisUtils.runScript(script, values);
		return e != null && e == 1;
	}
}
