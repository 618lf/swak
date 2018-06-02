package com.swak.cache.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.NameableCache;
import com.swak.cache.redis.operations.AsyncOperations;
import com.swak.utils.Lists;
import com.swak.utils.Maps;

import io.lettuce.core.ScriptOutputType;

/**
 * 响应式的实现
 * @author lifeng
 * @param <T>
 */
public class AsyncMultiMapCache<T> extends NameableCache implements AsyncMultiMap<String, T>{

	/** 序列化策略 **/
	private SerStrategy ser;
	
	public AsyncMultiMapCache(String name) {
		this(name, -1);
	}
	
	public AsyncMultiMapCache(final String name, final int timeToIdle) {
		super(name, timeToIdle);
	}
	
	public void setStrategy(SerStrategy ser) {
		this.ser = ser;
	}
	
	@Override
	public CompletionStage<Map<String, T>> get(String key) {
		if (this.isValid()) {
			return this._hget(key);
		}
		return AsyncOperations.hGetAll(this.getKeyName(key)).thenApply(values -> {
			Map<String, T> maps = Maps.newHashMap();
			values.keySet().stream().forEach(s ->{
				maps.put(SafeEncoder.encode(s), this.ser.deserialize(values.get(s)));
			});
			return maps;
		});
	}
	
	private CompletionStage<Map<String, T>> _hget(String key) {
		String script = Cons.MULTI_MAP_GET_LUA;
		byte[][] pvalues = new byte[][] {SafeEncoder.encode(this.getKeyName(key)),SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		CompletionStage<List<byte[]>> fvalues = AsyncOperations.runScript(script, ScriptOutputType.MULTI, pvalues);
		return fvalues.thenApply(values -> {
			Map<String, T> maps = Maps.newHashMap();
			if (values != null && !values.isEmpty()) {
				Iterator<byte[]> iterator = values.iterator();
				while (iterator.hasNext()) {
					maps.put(SafeEncoder.encode(iterator.next()), this.ser.deserialize(iterator.next()));
				}
			}
			return maps;
		});
	}

	@Override
	public CompletionStage<String> put(String key, Map<String, T> v) {
		if (this.isValid()) {
			return this._hput(key, v);
		}
		Map<byte[], byte[]> tuple = Maps.newHashMap();
		v.keySet().stream().forEach(s ->{
			tuple.put(SafeEncoder.encode(s), this.ser.serialize(v.get(s)));
		});
		return AsyncOperations.hMSet(key, tuple);
	}
	
	private CompletionStage<String> _hput(String key, Map<String, T> v) {
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
		CompletionStage<byte[]> bFuture = AsyncOperations.runScript(script.replaceAll("#KEYS#", keys.toString()), ScriptOutputType.VALUE, result);
		return bFuture.thenApply(b->{
			return SafeEncoder.encode(b);
		});
	}

	@Override
	public CompletionStage<Long> delete(String key) {
		return AsyncOperations.del(this.getKeyName(key));
	}

	@Override
	public CompletionStage<T> get(String key, String k2) {
		if (this.isValid()) {
			return this._hget(key, k2).thenApply(bs ->{
				return this.ser.deserialize(bs);
			});
		}
		return AsyncOperations.hGet(this.getKeyName(key), k2).thenApply(bs ->{
			return this.ser.deserialize(bs);
		});
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected CompletionStage<byte[]> _hget(String key, String k2) {
		String script = Cons.MAP_GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(k2), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return AsyncOperations.runScript(script, ScriptOutputType.VALUE, values);
	}

	@Override
	public CompletionStage<Boolean> put(String key, String k2, T v) {
		if (this.isValid()) {
			return _hput(key, k2, v);
		}
		return AsyncOperations.hSet(this.getKeyName(key), k2, this.ser.serialize(v));
	}
	
	/**
	 * 高性能put
	 * @param key
	 * @return
	 */
	protected CompletionStage<Boolean> _hput(String key, String k2, T v) {
		String script = Cons.MAP_PUT_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(k2), this.ser.serialize(v), SafeEncoder.encode(String.valueOf(this.getTimeToIdle()))};
		return AsyncOperations.runScript(script, ScriptOutputType.VALUE, values);
	}

	@Override
	public CompletionStage<Long> delete(String key, String k2) {
		return AsyncOperations.hDel(this.getKeyName(key), k2);
	}

	@Override
	public AsyncMultiMap<String, T> expire(int seconds) {
		this.setTimeToIdle(seconds);
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public AsyncMultiMap<String, String> primitive() {
		this.setStrategy(new PrimitiveStrategy());
		return (AsyncMultiMap<String, String>) this;
	}

	@Override
	public AsyncMultiMap<String, T> complex() {
		this.setStrategy(new ComplexStrategy());
		return this;
	}

}
