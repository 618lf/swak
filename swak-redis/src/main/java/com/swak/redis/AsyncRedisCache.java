package com.swak.redis;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.SafeEncoder;
import com.swak.cache.AsyncCache;
import com.swak.cache.CacheManager;
import com.swak.cache.Entity;
import com.swak.cache.LocalCache;
import com.swak.exception.SerializeException;
import com.swak.serializer.SerializationUtils;
import com.swak.utils.Lists;

public class AsyncRedisCache<T> extends NameableCache implements AsyncCache<T> {

	private RedisCacheManager cacheManager;

	/**
	 * 默认不过期
	 * 
	 * @param name
	 */
	public AsyncRedisCache(String name) {
		super(name);
	}

	/**
	 * 指定过期时间
	 * 
	 * @param name
	 * @param timeToIdle
	 */
	public AsyncRedisCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}

	/**
	 * 指定过期时间, 过期方式
	 * 
	 * @param name
	 * @param timeToIdle
	 * @param ideaAble
	 */
	public AsyncRedisCache(String name, int timeToIdle, boolean ideaAble) {
		super(name, timeToIdle, ideaAble);
	}

	/**
	 * 获得缓存管理器
	 * 
	 * @return
	 */
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public AsyncRedisCache<T> setCacheManager(RedisCacheManager cacheManager) {
		this.cacheManager = cacheManager;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CompletionStage<T> getObject(String key) {
		if (!idleAble()) {
			return this._get(key).thenApply((bs) -> {
				return (T) SerializationUtils.deserialize(bs);
			}).exceptionally(e -> {
				return this.exceptionalOnSerialize(key, e);
			});
		}
		return this._hget(key).thenApply((bs) -> {
			return (T) SerializationUtils.deserialize(bs);
		}).exceptionally(e -> {
			return this.exceptionalOnSerialize(key, e);
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public CompletionStage<T> getObjectAndDel(String key) {
		CompletableFuture<T> resultFuture = new CompletableFuture<>();
		this._get(key).thenApply((bs) -> {
			return (T) SerializationUtils.deserialize(bs);
		}).thenAcceptBoth(this._del(key), (r, n) -> {
			resultFuture.complete(r);
		}).exceptionally(v -> {
			resultFuture.complete(null);
			this.exceptionalOnSerialize(key, v);
			return null;
		});
		return resultFuture;
	}

	private T exceptionalOnSerialize(String key, Throwable e) {
		if (e instanceof SerializeException) {
			this._del(key);
		}
		return null;
	}

	@Override
	public CompletionStage<String> getString(String key) {
		if (!idleAble()) {
			return this._get(key).thenApply((bs) -> {
				return SafeEncoder.encode(bs);
			});
		}
		return this._hget(key).thenApply((bs) -> {
			return SafeEncoder.encode(bs);
		});
	}

	@Override
	public CompletionStage<String> getStringAndDel(String key) {
		CompletableFuture<String> resultFuture = new CompletableFuture<>();
		this._get(key).thenApply((bs) -> {
			return SafeEncoder.encode(bs);
		}).thenAcceptBoth(this._del(key), (r, n) -> {
			resultFuture.complete(r);
		}).exceptionally(v -> {
			resultFuture.completeExceptionally(v);
			return null;
		});
		return resultFuture;
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
		if (!idleAble()) {
			return _exists(key);
		}
		return _hexists(key);
	}

	@Override
	public CompletionStage<Entity<T>> putObject(String key, T value) {
		return this._set(key, SerializationUtils.serialize(value)).thenApply(s -> {
			return new Entity<T>(key, value);
		});
	}

	@Override
	public CompletionStage<Entity<String>> putString(String key, String value) {
		return this._set(key, SafeEncoder.encode(value)).thenApply(s -> {
			return new Entity<String>(key, value);
		});
	}

	@Override
	public CompletionStage<Long> ttl(String key) {
		return cacheManager.getRedisService().async().ttl(getKeyName(key));
	}

	/**
	 * 原生的获取
	 * 
	 * @param key
	 * @return
	 */
	protected CompletionStage<byte[]> _get(String key) {
		String keyName = this.getKeyName(key);
		return cacheManager.getRedisService().async().get(keyName);
	}

	/**
	 * 高性能get
	 * 
	 * @param key
	 * @return
	 */
	protected CompletionStage<byte[]> _hget(String key) {
		String script = Scripts.GET_LUA;
		byte[][] values = new byte[][] { SafeEncoder.encode(this.getKeyName(key)),
				SafeEncoder.encode(String.valueOf(this.getLifeTime())) };
		return cacheManager.getRedisService().async().runScript(script, ReturnType.VALUE, values, null);
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
			return cacheManager.getRedisService().async().set(keyName, value, this.getLifeTime());
		} else {
			return cacheManager.getRedisService().async().set(keyName, value);
		}
	}

	/**
	 * 删除当前的key
	 */
	protected CompletionStage<Long> _del(String... keys) {
		if (keys.length == 1) {
			return cacheManager.getRedisService().async().del(this.getKeyName(keys[0]));
		} else {
			List<String> _keys = Lists.newArrayList(keys.length);
			for (String key : keys) {
				_keys.add(this.getKeyName(key));
			}
			return cacheManager.getRedisService().async().del(_keys.toArray(keys));
		}
	}

	/**
	 * 当前key 是否存在
	 */
	protected CompletionStage<Long> _exists(String key) {
		return cacheManager.getRedisService().async().exists(this.getKeyName(key));
	}

	/**
	 * 高性能get
	 * 
	 * @param key
	 * @return
	 */
	protected CompletionStage<Long> _hexists(String key) {
		String script = Scripts.EXISTS_LUA;
		byte[][] values = new byte[][] { SafeEncoder.encode(this.getKeyName(key)),
				SafeEncoder.encode(String.valueOf(this.getLifeTime())) };
		return cacheManager.getRedisService().async().runScript(script, ReturnType.INTEGER, values, null);
	}

	/**
	 * 转换为二级缓存
	 */
	@Override
	public AsyncCache<T> wrapLocal() {
		LocalCache<Object> local = this.cacheManager.getLocalCache();
		return new AsyncRedisCacheChannel<T>(this, local);
	}
}
