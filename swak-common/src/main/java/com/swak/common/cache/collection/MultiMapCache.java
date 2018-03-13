package com.swak.common.cache.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.swak.common.cache.Cons;
import com.swak.common.cache.redis.NameableCache;
import com.swak.common.cache.redis.RedisUtils;
import com.swak.common.utils.Lists;
import com.swak.common.utils.Maps;

import redis.clients.util.SafeEncoder;

/**
 * value 是 一个 map
 * @author lifeng
 */
public class MultiMapCache<T> extends NameableCache implements MultiMap<String, T>{

	/** 序列化策略 **/
	private SerStrategy ser;
	
	public MultiMapCache(String name) {
		this(name, -1);
	}
	
	public MultiMapCache(final String name, final int timeToIdle) {
		super(name, timeToIdle);
	}
	
	public void setStrategy(SerStrategy ser) {
		this.ser = ser;
	}
	
	@Override
	public Map<String, T> get(String key) {
		if (this.isValid()) {
			return this._hget(key);
		}
		Map<String, byte[]> values = RedisUtils.getRedis().hGetAll(this.getKeyName(key));
		Map<String, T> maps = Maps.newHashMap();
		values.keySet().stream().forEach(s ->{
			maps.put(s, this.ser.deserialize(values.get(s)));
		});
		return maps;
	}
	
	private Map<String, T> _hget(String key) {
		Map<String, T> maps = Maps.newHashMap();
		String script = Cons.MULTI_MAP_GET_LUA;
		byte[][] pvalues = new byte[][] {SafeEncoder.encode(this.getKeyName(key)),SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		List<byte[]> values = RedisUtils.getRedis().runAndGetList(script, pvalues);
	    final Iterator<byte[]> iterator = values.iterator();
	    while (iterator.hasNext()) {
	    	maps.put(SafeEncoder.encode(iterator.next()), this.ser.deserialize(iterator.next()));
	    }
		return maps;
	}

	@Override
	public void put(String key, Map<String, T> v) {
		if (this.isValid()) {
			this._hput(key, v);
		} else {
			Map<String, byte[]> tuple = Maps.newHashMap();
			v.keySet().stream().forEach(s ->{
				tuple.put(s, this.ser.serialize(v.get(s)));
			});
			RedisUtils.getRedis().hMSet(this.getKeyName(key), tuple);
		}
	}
	
	private void _hput(String key, Map<String, T> v) {
		String script = Cons.MULTI_MAP_PUT_LUA;
		StringBuilder keys = new StringBuilder();
		List<byte[]> bytes = Lists.newArrayList();
		int i = 3;
		Iterator<String> it = v.keySet().iterator();
		while(it.hasNext()) {
			String s = it.next();
			bytes.add(SafeEncoder.encode(s));
			bytes.add(this.ser.serialize(v.get(s)));
			keys.append(", KEYS[").append(i++).append("]");
			keys.append(", KEYS[").append(i++).append("]");
		}
		byte[][] values = bytes.toArray(new byte[bytes.size()][]);
		byte[][] pvalues = new byte[][] {
			SafeEncoder.encode(this.getKeyName(key)),
			SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))
		};
		byte[][] result = new byte[values.length + pvalues.length][];
		System.arraycopy(pvalues, 0, result, 0, pvalues.length);  
		System.arraycopy(values, 0, result, pvalues.length, values.length);  
		RedisUtils.getRedis().run(script.replaceAll("#KEYS#", keys.toString()), result);
	}

	@Override
	public void delete(String key) {
		String keyName = this.getKeyName(key);
		RedisUtils.getRedis().delete(keyName);
	}

	@Override
	public T get(String key, String k2) {
		if (this.isValid()) {
			return this.ser.deserialize(this._hget(key, k2));
		}
		return this.ser.deserialize(RedisUtils.getRedis().hGet(this.getKeyName(key), k2));
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected byte[] _hget(String key, String k2) {
		String script = Cons.MAP_GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(k2), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return (byte[])RedisUtils.getRedis().runAndGetOne(script, values);
	}

	@Override
	public void pub(String key, String k2, T v) {
		if (this.isValid()) {
			_hput(key, k2, v);
		} else {
			RedisUtils.getRedis().hSet(this.getKeyName(key), k2, this.ser.serialize(v));
		}
	}
	
	/**
	 * 高性能put
	 * @param key
	 * @return
	 */
	protected void _hput(String key, String k2, T v) {
		String script = Cons.MAP_PUT_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(k2), this.ser.serialize(v), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
	    RedisUtils.getRedis().runAndGetOne(script, values);
	}

	@Override
	public void delete(String key, String k2) {
		RedisUtils.getRedis().hDel(this.getKeyName(key), k2);
	}
	
	/**
	 * 设置过期时间
	 * @param seconds
	 * @return
	 */
	public MultiMapCache<T> expire(int seconds) {
		this.setTimeToIdle(seconds);
		return this;
	}
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MultiMapCache<String> primitive() {
		this.setStrategy(new PrimitiveStrategy());
		return (MultiMapCache<String>) this;
	}
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	public MultiMapCache<T> complex() {
		this.setStrategy(new ComplexStrategy());
		return this;
	}
}