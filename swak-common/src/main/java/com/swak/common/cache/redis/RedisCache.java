package com.swak.common.cache.redis;

/**
 * Redis 需要在配置文件中配置,以后在添加详细的参数
 * 
 * @author lifeng
 */
public class RedisCache extends AbstractRedisCache {

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

	/**
	 * 原生的获取
	 * 
	 * @param key
	 * @return
	 */
	protected Object _get(String key) {
		String keyName = this.getKeyName(key);
		return RedisUtils.getRedis().getObject(keyName);
	}

	/**
	 * 原生的设置
	 * 
	 * @param key
	 * @param value
	 * @param expiration
	 */
	protected void _set(String key, Object value, int expiration) {
		String keyName = this.getKeyName(key);
		if (isValid(expiration)) {
			RedisUtils.getRedis().set(keyName, value, expiration);
		} else {
			RedisUtils.getRedis().set(keyName, value);
		}
	}
	
	/**
	 * 设置过期时间
	 * 
	 * @param key
	 */
	protected void _expire(String key) {
		String keyName = this.getKeyName(key);
		int expiration = this.getTimeToIdle();
		if (isValid(expiration)) {
			RedisUtils.getRedis().expire(keyName, expiration);
		}
	}
}
