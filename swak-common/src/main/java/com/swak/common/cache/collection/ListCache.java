package com.swak.common.cache.collection;

import com.swak.common.cache.Cons;
import com.swak.common.cache.redis.NameableCache;
import com.swak.common.cache.redis.RedisUtils;

import redis.clients.util.SafeEncoder;

/**
 * FIFO - 本身就是一个 list
 * 
 * @author lifeng
 */
public class ListCache<T> extends NameableCache implements CList<T> {

	/**
	 * 所有的列表都使用这个作为KEY
	 */
	private static String DEFAULT_KEY = "_LIST";
	
	/**
	 * 序列化策略
	 */
	private SerStrategy ser;

	public ListCache(String name) {
		this(name, -1);
	}
	
	public ListCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}
	
	public void setStrategy(SerStrategy ser) {
		this.ser = ser;
	}

	/**
	 * 插入数据
	 */
	@Override
	public void push(T t) {
		if (this.isValid()) {
			this._hpush(t);
		} else {
			RedisUtils.getRedis().lPush(this.getKeyName(null), this.ser.serialize(t));
		}
	}
	
	/**
	 * 高性能put
	 * @param key
	 * @return
	 */
	protected void _hpush(T t) {
		String script = Cons.LIST_PUT_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(null)), this.ser.serialize(t), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
	    RedisUtils.getRedis().runAndGetOne(script, values);
	}

	/**
	 * 获得数据
	 */
	@Override
	public T pop() {
		if (this.isValid()) {
			return this.ser.deserialize(this._hpop());
		}
		return this.ser.deserialize(RedisUtils.getRedis().lPop(this.getKeyName(null)));
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected byte[] _hpop() {
		String script = Cons.LIST_GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(null)), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
	    return (byte[])RedisUtils.getRedis().runAndGetOne(script, values);
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
	public ListCache<T> expire(int seconds) {
		this.setTimeToIdle(seconds);
		return this;
	}
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ListCache<String> primitive() {
		this.setStrategy(new PrimitiveStrategy());
		return (ListCache<String>) this;
	}
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	public ListCache<T> complex() {
		this.setStrategy(new ComplexStrategy());
		return this;
	}
}