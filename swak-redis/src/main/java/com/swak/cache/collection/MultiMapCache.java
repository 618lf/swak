package com.swak.cache.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.NameableCache;
import com.swak.cache.redis.operations.SyncOperations;
import com.swak.utils.Lists;
import com.swak.utils.Maps;

import io.lettuce.core.ScriptOutputType;

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
		Map<byte[], byte[]> values = SyncOperations.hGetAll(this.getKeyName(key));
		Map<String, T> maps = Maps.newHashMap();
		values.keySet().stream().forEach(s ->{
			maps.put(SafeEncoder.encode(s), this.ser.deserialize(values.get(s)));
		});
		return maps;
	}
	
	private Map<String, T> _hget(String key) {
		Map<String, T> maps = Maps.newHashMap();
		String script = Cons.MULTI_MAP_GET_LUA;
		byte[][] pvalues = new byte[][] {SafeEncoder.encode(this.getKeyName(key)),SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		List<byte[]> values = SyncOperations.runScript(script, ScriptOutputType.MULTI, pvalues);
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
			Map<byte[], byte[]> tuple = Maps.newHashMap();
			v.keySet().stream().forEach(s ->{
				tuple.put(SafeEncoder.encode(s), this.ser.serialize(v.get(s)));
			});
			SyncOperations.hMSet(this.getKeyName(key), tuple);
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
		SyncOperations.runScript(script.replaceAll("#KEYS#", keys.toString()), ScriptOutputType.VALUE, result);
	}

	@Override
	public void delete(String key) {
		SyncOperations.del(this.getKeyName(key));
	}

	@Override
	public T get(String key, String k2) {
		if (this.isValid()) {
			return this.ser.deserialize(this._hget(key, k2));
		}
		return this.ser.deserialize(SyncOperations.hGet(this.getKeyName(key), k2));
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected byte[] _hget(String key, String k2) {
		String script = Cons.MAP_GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(k2), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return SyncOperations.runScript(script, ScriptOutputType.VALUE, values);
	}

	@Override
	public void pub(String key, String k2, T v) {
		if (this.isValid()) {
			_hput(key, k2, v);
		} else {
			SyncOperations.hSet(this.getKeyName(key), k2, this.ser.serialize(v));
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
		SyncOperations.runScript(script, ScriptOutputType.VALUE, values);
	}

	@Override
	public void delete(String key, String k2) {
		SyncOperations.hDel(this.getKeyName(key), k2);
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