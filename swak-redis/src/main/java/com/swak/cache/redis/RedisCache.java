package com.swak.cache.redis;

import java.util.List;

import com.swak.cache.AsyncCache;
import com.swak.cache.Cache;
import com.swak.cache.Cons;
import com.swak.cache.ReactiveCache;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.operations.SyncOperations;
import com.swak.serializer.SerializationUtils;
import com.swak.utils.Lists;

import io.lettuce.core.ScriptOutputType;

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
	public Boolean exists(String key) {
		if (!isValid()) {
			return _exists(key);
		}
		return _hexists(key);
	}

	@Override
	public Long delete(String key) {
		return this._del(key);
	}

	@Override
	public Long delete(String ... keys) {
		return this._del(keys);
	}

	@Override
	public String putObject(String key, T value) {
		return this._set(key, SerializationUtils.serialize(value));
	}
	
	@Override
	public String putString(String key, String value) {
		return this._set(key, SafeEncoder.encode(value));
	}

	@Override
	public Long ttl(String key) {
		return SyncOperations.ttl(getKeyName(key));
	}

	/**
	 * 原生的获取
	 * 
	 * @param key
	 * @return
	 */
	protected byte[] _get(String key) {
		String keyName = this.getKeyName(key);
		return SyncOperations.get(keyName);
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected byte[] _hget(String key) {
		String script = Cons.GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return SyncOperations.runScript(script, ScriptOutputType.VALUE, values);
	}
	
	/**
	 * 原生的设置
	 * 
	 * @param key
	 * @param value
	 * @param expiration
	 */
	protected String _set(String key, byte[] value) {
		String keyName = this.getKeyName(key);
		if (isValid()) {
			return SyncOperations.set(key, value, this.timeToIdle);
		} else {
			return SyncOperations.set(keyName, value);
		}
	}
	
	/**
	 * 删除当前的key
	 */
	protected Long _del(String... keys) {
		if (keys.length == 1) {
			return SyncOperations.del(this.getKeyName(keys[0]));
		} else {
			List<String> _keys = Lists.newArrayList(keys.length);
			for(String key: keys) {
				_keys.add(this.getKeyName(key));
			}
			return SyncOperations.del(_keys.toArray(keys));
		}
	}

	/**
	 * 当前key 是否存在
	 */
	protected Boolean _exists(String key) {
		Long count = SyncOperations.exists(this.getKeyName(key));
		return count != null && count >0;
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected Boolean _hexists(String key) {
		String script = Cons.EXISTS_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		Long count = SyncOperations.runScript(script, ScriptOutputType.VALUE, values);
		return count != null && count >0;
	}
	
	// -------------  提供的异步化支持 ------------
	@Override
	public AsyncCache<T> async() {
		return new AsyncRedisCache<T>(this.name, this.timeToIdle);
	}
	@Override
	public ReactiveCache<T> reactive() {
		return new ReactiveRedisCache<T>(this.name, this.timeToIdle);
	}
}