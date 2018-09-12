package com.swak.cache.redis;

import java.util.List;

import com.swak.cache.Cons;
import com.swak.cache.Entity;
import com.swak.cache.ReactiveCache;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.operations.ReactiveOperations;
import com.swak.serializer.SerializationUtils;
import com.swak.utils.Lists;

import io.lettuce.core.ScriptOutputType;
import reactor.core.publisher.Mono;

/**
 * 响应式 cache
 * @author lifeng
 *
 * @param <T>
 */
public class ReactiveRedisCache<T> extends NameableCache implements ReactiveCache<T>{

	/**
	 * 默认不过期
	 * @param name
	 */
	public ReactiveRedisCache(String name) {
		super(name);
	}
	
	/**
	 * 指定过期时间
	 * @param name
	 * @param timeToIdle
	 */
	public ReactiveRedisCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Mono<T> getObject(String key) {
		if (!isValid()) {
			return this._get(key).map((bs) ->{
				return (T) SerializationUtils.deserialize(bs);
			}); 
		}
		return this._hget(key).map((bs) ->{
			return (T) SerializationUtils.deserialize(bs);
		}); 
	}

	@Override
	public Mono<String> getString(String key) {
		if (!isValid()) {
			return this._get(key).map((bs) ->{
				return SafeEncoder.encode(bs);
			}); 
		}
		return this._hget(key).map((bs) ->{
			return SafeEncoder.encode(bs);
		}); 
	}

	@Override
	public Mono<Long> delete(String key) {
		return this._del(key);
	}

	@Override
	public Mono<Long> delete(String... keys) {
		return this._del(keys);
	}

	@Override
	public Mono<Long> exists(String key) {
		if (!isValid()) {
			return _exists(key);
		}
		return _hexists(key);
	}

	@Override
	public Mono<Entity<T>> putObject(String key, T value) {
		return this._set(key, SerializationUtils.serialize(value)).map(s ->{
			return new Entity<T>(key, value);
		});
	}

	@Override
	public Mono<Entity<String>> putString(String key, String value) {
		return this._set(key, SafeEncoder.encode(value)).map(s ->{
			return new Entity<String>(key, value);
		});
	}

	@Override
	public Mono<Long> ttl(String key) {
		return ReactiveOperations.ttl(getKeyName(key));
	}
	
	/**
	 * 原生的获取
	 * 
	 * @param key
	 * @return
	 */
	protected Mono<byte[]> _get(String key) {
		String keyName = this.getKeyName(key);
		return ReactiveOperations.get(keyName);
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected Mono<byte[]> _hget(String key) {
		String script = Cons.GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return Mono.from(ReactiveOperations.runScript(script, ScriptOutputType.VALUE, values));
	}
	
	/**
	 * 原生的设置
	 * 
	 * @param key
	 * @param value
	 * @param expiration
	 */
	protected Mono<String> _set(String key, byte[] value) {
		String keyName = this.getKeyName(key);
		if (isValid()) {
			return ReactiveOperations.set(keyName, value, this.timeToIdle);
		} else {
			return ReactiveOperations.set(keyName, value);
		}
	}
	
	/**
	 * 删除当前的key
	 */
	protected Mono<Long> _del(String... keys) {
		if (keys.length == 1) {
			return ReactiveOperations.del(this.getKeyName(keys[0]));
		} else {
			List<String> _keys = Lists.newArrayList(keys.length);
			for(String key: keys) {
				_keys.add(this.getKeyName(key));
			}
			return ReactiveOperations.del(_keys.toArray(keys));
		}
	}

	/**
	 * 当前key 是否存在
	 */
	protected Mono<Long> _exists(String key) {
		return ReactiveOperations.exists(this.getKeyName(key));
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected Mono<Long> _hexists(String key) {
		String script = Cons.EXISTS_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return Mono.from(ReactiveOperations.runScript(script, ScriptOutputType.INTEGER, values));
	}
}
