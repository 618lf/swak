package com.swak.redis;

import java.util.List;

import com.swak.SafeEncoder;
import com.swak.cache.AsyncCache;
import com.swak.cache.Cache;
import com.swak.cache.Entity;
import com.swak.cache.LocalCache;
import com.swak.exception.SerializeException;
import com.swak.serializer.SerializationUtils;
import com.swak.utils.Lists;

/**
 * Redis 需要在配置文件中配置,以后在添加详细的参数
 * 
 * 如果有过期时间则每次查询会通过lua来执行，有20%的性能提升
 * 
 * @author lifeng
 */
public class RedisCache<T> extends NameableCache implements Cache<T> {

	private RedisCacheManager cacheManager;

	/**
	 * 默认不过期
	 * 
	 * @param name
	 */
	public RedisCache(String name) {
		super(name);
	}

	/**
	 * 指定过期时间
	 * 
	 * @param name
	 * @param timeToIdle
	 */
	public RedisCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}

	/**
	 * 指定过期时间, 过期方式
	 * 
	 * @param name
	 * @param timeToIdle
	 */
	public RedisCache(String name, int timeToIdle, boolean idleAble) {
		super(name, timeToIdle, idleAble);
	}

	/**
	 * 设置缓存管理器
	 * 
	 * @param cacheManager
	 * @return
	 */
	public RedisCache<T> setCacheManager(RedisCacheManager cacheManager) {
		this.cacheManager = cacheManager;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getObject(String key) {
		try {
			if (!idleAble()) {
				return (T) SerializationUtils.deserialize(this._get(key));
			}
			return (T) SerializationUtils.deserialize(this._hget(key));
		} catch (SerializeException e) {
			this._del(key);
			return null;
		}
	}

	@Override
	public String getString(String key) {
		if (!idleAble()) {
			return SafeEncoder.encode(this._get(key));
		}
		return SafeEncoder.encode(this._hget(key));
	}

	@Override
	public Boolean exists(String key) {
		if (!idleAble()) {
			return _exists(key);
		}
		return _hexists(key);
	}

	@Override
	public Long delete(String key) {
		return this._del(key);
	}

	@Override
	public Long delete(String... keys) {
		return this._del(keys);
	}

	@Override
	public Entity<T> putObject(String key, T value) {
		this._set(key, SerializationUtils.serialize(value));
		return new Entity<T>(key, value);
	}

	@Override
	public Entity<String> putString(String key, String value) {
		this._set(key, SafeEncoder.encode(value));
		return new Entity<String>(key, value);
	}

	@Override
	public Long ttl(String key) {
		return cacheManager.getRedisService().sync().ttl(getKeyName(key));
	}

	/**
	 * 原生的获取
	 * 
	 * @param key
	 * @return
	 */
	protected byte[] _get(String key) {
		String keyName = this.getKeyName(key);
		return cacheManager.getRedisService().sync().get(keyName);
	}

	/**
	 * 高性能get
	 * 
	 * @param key
	 * @return
	 */
	protected byte[] _hget(String key) {
		String script = Scripts.GET_LUA;
		byte[][] values = new byte[][] { SafeEncoder.encode(this.getKeyName(key)),
				SafeEncoder.encode(String.valueOf(this.getLifeTime())) };
		return cacheManager.getRedisService().sync().runScript(script, ReturnType.VALUE, values, null);
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
			return cacheManager.getRedisService().sync().set(keyName, value, this.getLifeTime());
		} else {
			return cacheManager.getRedisService().sync().set(keyName, value);
		}
	}

	/**
	 * 删除当前的key
	 */
	protected Long _del(String... keys) {
		if (keys.length == 1) {
			return cacheManager.getRedisService().sync().del(this.getKeyName(keys[0]));
		} else {
			List<String> _keys = Lists.newArrayList(keys.length);
			for (String key : keys) {
				_keys.add(this.getKeyName(key));
			}
			return cacheManager.getRedisService().sync().del(_keys.toArray(keys));
		}
	}

	/**
	 * 当前key 是否存在
	 */
	protected Boolean _exists(String key) {
		Long count = cacheManager.getRedisService().sync().exists(this.getKeyName(key));
		return count != null && count > 0;
	}

	/**
	 * 高性能get
	 * 
	 * @param key
	 * @return
	 */
	protected Boolean _hexists(String key) {
		String script = Scripts.EXISTS_LUA;
		byte[][] values = new byte[][] { SafeEncoder.encode(this.getKeyName(key)),
				SafeEncoder.encode(String.valueOf(this.getLifeTime())) };
		Long count = cacheManager.getRedisService().sync().runScript(script, ReturnType.INTEGER, values, null);
		return count != null && count > 0;
	}

	// ------------- 提供的异步化支持 ------------
	@Override
	public AsyncCache<T> async() {
		return new AsyncRedisCache<T>(this.name, this.lifeTime, this.idleAble).setCacheManager(cacheManager);
	}

	@Override
	public RedisCacheChannel<T> wrapLocal() {
		LocalCache<Object> local = this.cacheManager.getLocalCache();
		return new RedisCacheChannel<T>(this, local);
	}
}