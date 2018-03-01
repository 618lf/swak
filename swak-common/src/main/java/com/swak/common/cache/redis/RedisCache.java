package com.swak.common.cache.redis;

import java.util.List;

import com.swak.common.cache.Cache;
import com.swak.common.utils.Lists;

/**
 * Redis 需要在配置文件中配置,以后在添加详细的参数
 * 
 * @author lifeng
 */
public class RedisCache implements Cache {

	private static int EXPIRATION_IN = -1; // 默认不过期
	private String name;
	private String prex = "#";
	private int timeToLive = EXPIRATION_IN;// 固定的存活时间，到点就清除
	private int timeToIdle = EXPIRATION_IN;// 最大空闲时间，每次访问会修改最大的空闲时间

	public RedisCache() {
	}

	public RedisCache(String name) {
		this.setName(name);
	}

	/**
	 * 得到key 的string表示(全部使用string的key)
	 * 
	 * @param key
	 * @return
	 */
	protected String getKeyName(Object key) {
		return new StringBuilder(name).append(prex).append(key).toString();
	}

	/**
	 * 得到缓存的名称
	 */
	@Override
	public String getName() {
		return name.toString();
	}

	/**
	 * 得到本地存储对象 直接返回缓存（作为最小的存储单元，保护后面的连接）
	 */
	@Override
	public Object getNativeCache() {
		return this;
	}

	/**
	 * 类似最大空闲时间 get,如果设置了过期时间，每次获取会更新过期时间,默认的实现 如果不需要这样的功能，可以使用 RedisUtils 来获取
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		if (key == null) {
			return null;
		}
        this.exists(key);
		return (T) this._get(key);
	}

	/**
	 * 添加
	 */
	@Override
	public void put(String key, Object value) {
		String _key = getKeyName(key);
		int expiration = this.getExpiration(); // 设置过期时间
		this._set(_key, value, expiration);
	}

	/**
	 * 删除 pattern 指定的key
	 * 
	 * @param pattern
	 */
	@Override
	public void delete(String key) {
		RedisUtils.getRedis().delete(getKeyName(key));
	}

	@Override
	public void delete(List<String> keys) {
		if (keys != null && keys.size() != 0) {
			List<String> _keys = Lists.newArrayList();
			for (Object key : keys) {
				_keys.add(this.getKeyName(key));
			}
			RedisUtils.getRedis().delete(_keys.toArray(new String[] {}));
		}
	}

	@Override
	public boolean exists(String key) {
		this._expire(key);
		return RedisUtils.getRedis().exists(getKeyName(key));
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
	protected Object _get(String key) {
		return RedisUtils.getRedis().getObject(key);
	}

	/**
	 * 原生的设置
	 * 
	 * @param key
	 * @param value
	 * @param expiration
	 */
	protected void _set(String key, Object value, int expiration) {
		if (ExpireTimeValueWrapper.isValid(expiration)) {
			RedisUtils.getRedis().set(key, value, expiration);
		} else {
			RedisUtils.getRedis().set(key, value);
		}
	}

	/**
	 * 设置过期时间
	 * 
	 * @param key
	 */
	protected void _expire(String key) {
		String _key = this.getKeyName(key);
		int expiration = this.getTimeToIdle();
		if (ExpireTimeValueWrapper.isValid(expiration)) {// 设置了空闲时间的key每次访问才会更新过期时间为最大空闲时间，不会累加
			RedisUtils.getRedis().expire(_key, expiration);
		}
	}

	/**
	 * 默认值设置过期时间
	 * 
	 * @return
	 */
	protected int getExpiration() {
		// 这两个只能使用一个,简单点
		if (ExpireTimeValueWrapper.isValid(this.timeToLive)) {
			return this.timeToLive;
		}
		if (ExpireTimeValueWrapper.isValid(this.timeToIdle)) {
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
	protected int getExpiration(int timeToLive, int timeToIdle) {
		if (ExpireTimeValueWrapper.isValid(timeToLive)) {
			return timeToLive;
		}
		if (ExpireTimeValueWrapper.isValid(timeToIdle)) {
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

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public int getTimeToIdle() {
		return timeToIdle;
	}

	public void setTimeToIdle(int timeToIdle) {
		this.timeToIdle = timeToIdle;
	}
}
