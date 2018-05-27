package com.swak.cache.redis;

import java.util.List;
import java.util.concurrent.CompletionStage;

import com.swak.cache.AsyncCache;
import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.operations.AsyncOperations;
import com.swak.serializer.SerializationUtils;
import com.swak.utils.Lists;

public class AsyncRedisCache<T> extends NameableCache implements AsyncCache<T> {

	/**
	 * 默认不过期
	 * @param name
	 */
	public AsyncRedisCache(String name) {
		super(name);
	}
	
	/**
	 * 指定过期时间
	 * @param name
	 * @param timeToIdle
	 */
	public AsyncRedisCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}

	@Override
	@SuppressWarnings("unchecked")
	public CompletionStage<T> getObject(String key) {
		if (!isValid()) {
			return this._get(key).thenApply((bs) ->{
				return (T) SerializationUtils.deserialize(bs);
			}); 
		}
		return this._hget(key).thenApply((bs) ->{
			return (T) SerializationUtils.deserialize(bs);
		}); 
	}

	@Override
	public CompletionStage<String> getString(String key) {
		if (!isValid()) {
			return this._get(key).thenApply((bs) ->{
				return SafeEncoder.encode(bs);
			}); 
		}
		return this._hget(key).thenApply((bs) ->{
			return SafeEncoder.encode(bs);
		}); 
	}

	@Override
	public CompletionStage<Long> delete(String key) {
		return this._del(key);
	}

	@Override
	public CompletionStage<Long> delete(String... keys) {
		return this._del(keys);
	}

	@Override
	public CompletionStage<Long> exists(String key) {
		return this.exists(key);
	}

	@Override
	public CompletionStage<String> putObject(String key, T value) {
		return this._set(key, SerializationUtils.serialize(value));
	}

	@Override
	public CompletionStage<String> putString(String key, String value) {
		return this._set(key, SafeEncoder.encode(value));
	}

	@Override
	public CompletionStage<Long> ttl(String key) {
		return AsyncOperations.ttl(getKeyName(key));
	}
	
	/**
	 * 原生的获取
	 * 
	 * @param key
	 * @return
	 */
	protected CompletionStage<byte[]> _get(String key) {
		String keyName = this.getKeyName(key);
		return AsyncOperations.get(keyName);
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected CompletionStage<byte[]> _hget(String key) {
		String script = Cons.GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return AsyncOperations.runScript(script, values);
	}
	
	/**
	 * 原生的设置
	 * 
	 * @param key
	 * @param value
	 * @param expiration
	 */
	protected CompletionStage<String> _set(String key, byte[] value) {
		String keyName = this.getKeyName(key);
		if (isValid()) {
			return AsyncOperations.set(keyName, value, this.timeToIdle);
		} else {
			return AsyncOperations.set(keyName, value);
		}
	}
	
	/**
	 * 删除当前的key
	 */
	protected CompletionStage<Long> _del(String... keys) {
		if (keys.length == 1) {
			return AsyncOperations.del(this.getKeyName(keys[0]));
		} else {
			List<String> _keys = Lists.newArrayList(keys.length);
			for(String key: keys) {
				_keys.add(this.getKeyName(key));
			}
			return AsyncOperations.del(_keys.toArray(keys));
		}
	}

	/**
	 * 当前key 是否存在
	 */
	protected CompletionStage<Long> _exists(String key) {
		return AsyncOperations.exists(this.getKeyName(key));
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected CompletionStage<Long> _hexists(String key) {
		String script = Cons.EXISTS_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return AsyncOperations.runScript(script, values);
	}
}
