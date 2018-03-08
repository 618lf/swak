package com.swak.common.cache.redis;

import java.util.List;

import com.swak.common.cache.Cache;
import com.swak.common.utils.Lists;

public abstract class AbstractRedisCache implements Cache{

	private static int EXPIRATION_IN = -1; // 默认不过期
	private String name;
	private String prex = "#";
	private int timeToIdle = EXPIRATION_IN;// 最大空闲时间，每次访问会修改最大的空闲时间
	
	/**
	 * 默认不过期
	 * @param name
	 */
	public AbstractRedisCache(String name) {
		this.setName(name);
	}
	
	/**
	 * 指定过期时间
	 * @param name
	 * @param timeToIdle
	 */
	public AbstractRedisCache(String name, int timeToIdle) {
		this.setName(name);
		this.setTimeToIdle(timeToIdle);
	}
	
	/**
	 * 得到默认的缓存
	 * @return
	 */
	public Object getNativeCache() {
		return this;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		this._expire(key);
		return (T) this._get(key);
	}

	@Override
	public void delete(String key) {
		RedisUtils.getRedis().delete(getKeyName(key));
	}

	@Override
	public void delete(List<String> keys) {
		if (keys != null && keys.size() != 0) {
			List<String> _keys = Lists.newArrayList();
			for (String key : keys) {
				_keys.add(this.getKeyName(key));
			}
			RedisUtils.getRedis().delete(_keys.toArray(new String[] {}));
		}
	}

	@Override
	public boolean exists(String key) {
		String keyName = getKeyName(key);
		this._expire(key);
		return RedisUtils.getRedis().exists(keyName);
	}

	@Override
	public void put(String key, Object value) {
		int expiration = this.getExpiration(); // 设置过期时间
		this._set(key, value, expiration);
	}

	@Override
	public void clear() {
		String _key = new StringBuilder(name).append(prex).append("*").toString();
		RedisUtils.getRedis().deletes(_key);
	}

	@Override
	public long ttl(String key) {
		String _key = getKeyName(key);
		return RedisUtils.getRedis().ttl(_key);
	}
	
	
	/**
	 * 原生的获取
	 * 
	 * @param key
	 * @return
	 */
	protected abstract Object _get(String key);

	/**
	 * 原生的设置
	 * 
	 * @param key
	 * @param value
	 * @param expiration
	 */
	protected abstract void _set(String key, Object value, int expiration);
	
	/**
	 * 设置过期时间
	 * 
	 * @param key
	 */
	protected abstract void _expire(String key);
	
	/**
	 * 得到key 的string表示(全部使用string的key)
	 * 
	 * @param key
	 * @return
	 */
	protected String getKeyName(String key) {
		return new StringBuilder(name).append(prex).append(key).toString();
	}
	
	/**
	 * 默认值设置过期时间
	 * 
	 * @return
	 */
	protected int getExpiration() {
		if (isValid(this.timeToIdle)) {
			return this.timeToIdle;
		}
		return EXPIRATION_IN;
	}

	/**
	 * 动态设置过期时间(设置了无效的动态时间)
	 * 
	 * @param timeToLive
	 * @param timeToIdle
	 * @return
	 */
	protected int getExpiration(int timeToIdle) {
		if (isValid(timeToIdle)) {
			return timeToIdle;
		}
		return EXPIRATION_IN;
	}

	public void setName(String name) {
		if (name != null) {
			this.name = name.toUpperCase();
		}
	}

	public String getPrex() {
		return prex;
	}

	public void setPrex(String prex) {
		this.prex = prex;
	}

	public int getTimeToIdle() {
		return timeToIdle;
	}

	public void setTimeToIdle(int timeToIdle) {
		this.timeToIdle = timeToIdle;
	}
	
	/**
	 * 设置的时间是有效的
	 * @param time
	 * @return
	 */
	public boolean isValid(int time) {
		return time > 0;
	}
}
