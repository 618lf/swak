package com.swak.cache.redis;

import com.swak.cache.redis.operations.SyncOperations;

public abstract class NameableCache {

	private static int EXPIRATION_IN = -1; // 默认不过期
	protected String name;
	protected String prex = "#";
	protected int timeToIdle = EXPIRATION_IN;// 最大空闲时间，每次访问会修改最大的空闲时间
	
	/**
	 * 默认不过期
	 * @param name
	 */
	public NameableCache(String name) {
		this.setName(name);
	}
	
	/**
	 * 指定过期时间
	 * @param name
	 * @param timeToIdle
	 */
	public NameableCache(String name, int timeToIdle) {
		this.setName(name);
		this.setTimeToIdle(timeToIdle);
	}
	
	public String getName() {
		return this.name;
	}

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
	 * 设置过期时间
	 * 
	 * @param key
	 */
	protected void expire(String key) {
		if (isValid()) {
			SyncOperations.expire(this.getKeyName(key), this.timeToIdle);
		}
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
	public boolean isValid() {
		return this.timeToIdle > 0;
	}
}
