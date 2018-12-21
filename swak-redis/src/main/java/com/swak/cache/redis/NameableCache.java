package com.swak.cache.redis;

/**
 * Cache 基本结构
 * 
 * @author lifeng
 */
public abstract class NameableCache {

	public static final int DEFAULT_LIFE_TIME = -1; // 默认不过期
	public static final boolean DEFAULT_IDEA_LIFE = true; // 默认不过期
	protected final String name;
	protected String prex = "#";
	protected int lifeTime = DEFAULT_LIFE_TIME;// 生命期，默认永久
	protected boolean idleAble = DEFAULT_IDEA_LIFE; // lifeTime 最大空闲时间，每次访问会修改最大的空闲时间
	
	/**
	 * 默认不过期
	 * @param name
	 */
	public NameableCache(String name) {
		this.name = name.toUpperCase();
	}
	
	/**
	 * 指定过期时间
	 * @param name
	 * @param timeToIdle
	 */
	public NameableCache(String name, int lifeTime) {
		this.name = name.toUpperCase();
		this.lifeTime = lifeTime;
	}
	
	/**
	 * 指定过期时间
	 * @param name
	 * @param timeToIdle
	 */
	public NameableCache(String name, int lifeTime, boolean idleAble) {
		this.name = name.toUpperCase();
		this.lifeTime = lifeTime;
		this.idleAble = idleAble;
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
	
	public String getPrex() {
		return prex;
	}

	public void setPrex(String prex) {
		this.prex = prex;
	}
	
	public int getLifeTime() {
		return lifeTime;
	}

	/**
	 * 设置的时间是有效的
	 * 
	 * @param time
	 * @return
	 */
	public boolean isValid() {
		return this.lifeTime > 0;
	}
	
	/**
	 * 设置的时间是有效的
	 * 
	 * @param time
	 * @return
	 */
	public boolean idleAble() {
		return idleAble & this.lifeTime > 0;
	}
}
