package com.swak.common.cache.collection;

import com.swak.common.cache.Cons;
import com.swak.common.cache.SafeEncoder;
import com.swak.common.cache.redis.NameableCache;
import com.swak.common.cache.redis.RedisUtils;

/**
 * 是一个大Map
 * @author lifeng
 */
public class MapCache<T> extends NameableCache implements CMap<String, T>{

	/**
	 * 所有的列表都使用这个作为KEY
	 */
	private static String DEFAULT_KEY = "_MAP";
	
	/**
	 * 序列化策略
	 */
	private SerStrategy ser;
	
	public MapCache(String name) {
		this(name, -1);
	}
	
	public MapCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}
	
	public void setStrategy(SerStrategy ser) {
		this.ser = ser;
	}
	
	@Override
	public T get(String k) {
		if (this.isValid()) {
			return this.ser.deserialize(this._hget(k));
		}
		return this.ser.deserialize(RedisUtils.hGet(this.getKeyName(null), k));
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected byte[] _hget(String k) {
		String script = Cons.MAP_GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(null)), SafeEncoder.encode(k), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
	    return RedisUtils.runScript(script, null, values);
	}

	@Override
	public void put(String k, T v) {
		if (this.isValid()) {
			this._hput(k, v);
		} else {
			RedisUtils.hSet(this.getKeyName(null), k, this.ser.serialize(v));
		}
	}
	
	/**
	 * 高性能put
	 * @param key
	 * @return
	 */
	protected void _hput(String k, T v) {
		String script = Cons.MAP_PUT_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(null)), SafeEncoder.encode(k), this.ser.serialize(v), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		RedisUtils.runScript(script, null, values);
	}

	@Override
	public void delete(String k) {
		RedisUtils.hDel(this.getKeyName(null), k);
	}
	
	/**
	 * redis list 的名称
	 * @return
	 */
	protected String getKeyName(String key) {
		return super.getKeyName(DEFAULT_KEY);
	}
	
	/**
	 * 设置过期时间
	 * @param seconds
	 * @return
	 */
	public MapCache<T> expire(int seconds) {
		this.setTimeToIdle(seconds);
		return this;
	}
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MapCache<String> primitive() {
		this.setStrategy(new PrimitiveStrategy());
		return (MapCache<String>) this;
	}
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	public MapCache<T> complex() {
		this.setStrategy(new ComplexStrategy());
		return this;
	}
}