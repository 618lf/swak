package com.swak.cache.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.NameableCache;
import com.swak.cache.redis.operations.ReactiveOperations;
import com.swak.utils.Lists;
import com.swak.utils.Maps;

import io.lettuce.core.ScriptOutputType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 响应式的实现
 * @author lifeng
 * @param <T>
 */
public class ReactiveMultiMapCache<T> extends NameableCache implements ReactiveMultiMap<String, T>{

	/** 序列化策略 **/
	private SerStrategy ser;
	
	public ReactiveMultiMapCache(String name) {
		this(name, -1);
	}
	
	public ReactiveMultiMapCache(final String name, final int timeToIdle) {
		super(name, timeToIdle);
	}
	
	public void setStrategy(SerStrategy ser) {
		this.ser = ser;
	}
	
	@Override
	public Mono<Map<String, T>> get(String key) {
		if (this.idleAble()) {
			return this._hget(key);
		}
		return ReactiveOperations.hGetAll(this.getKeyName(key)).map(values -> {
			Map<String, T> maps = Maps.newHashMap();
			values.keySet().stream().forEach(s ->{
				maps.put(SafeEncoder.encode(s), this.ser.deserialize(values.get(s)));
			});
			return maps;
		});
	}
	
	private Mono<Map<String, T>> _hget(String key) {
		String script = Cons.MULTI_MAP_GET_LUA;
		byte[][] pvalues = new byte[][] {SafeEncoder.encode(this.getKeyName(key)),SafeEncoder.encode(String.valueOf(this.getLifeTime()))};
		Flux<List<byte[]>> fvalues = ReactiveOperations.runScript(script, ScriptOutputType.MULTI, pvalues);
		return Mono.from(fvalues).map(values -> {
			Map<String, T> maps = Maps.newHashMap();
			Iterator<byte[]> iterator = values.iterator();
			while (iterator.hasNext()) {
				maps.put(SafeEncoder.encode(iterator.next()), this.ser.deserialize(iterator.next()));
			}
			return maps;
		});
	}

	@Override
	public Mono<String> put(String key, Map<String, T> v) {
		if (this.isValid()) {
			return this._hput(key, v);
		}
		Map<byte[], byte[]> tuple = Maps.newHashMap();
		v.keySet().stream().forEach(s ->{
			tuple.put(SafeEncoder.encode(s), this.ser.serialize(v.get(s)));
		});
		return ReactiveOperations.hMSet(key, tuple);
	}
	
	private Mono<String> _hput(String key, Map<String, T> v) {
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
			SafeEncoder.encode(String.valueOf(this.getLifeTime()))
		};
		byte[][] result = new byte[values.length + pvalues.length][];
		System.arraycopy(pvalues, 0, result, 0, pvalues.length);  
		System.arraycopy(values, 0, result, pvalues.length, values.length);  
		return Mono.from(ReactiveOperations.runScript(script.replaceAll("#KEYS#", keys.toString()), ScriptOutputType.VALUE, result));
	}

	@Override
	public Mono<Long> delete(String key) {
		return ReactiveOperations.del(this.getKeyName(key));
	}

	@Override
	public Mono<T> get(String key, String k2) {
		if (this.idleAble()) {
			return this._hget(key, k2).map(bs ->{
				return this.ser.deserialize(bs);
			});
		}
		return ReactiveOperations.hGet(this.getKeyName(key), k2).map(bs ->{
			return this.ser.deserialize(bs);
		});
	}
	
	/**
	 * 高性能get
	 * @param key
	 * @return
	 */
	protected Mono<byte[]> _hget(String key, String k2) {
		String script = Cons.MAP_GET_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(k2), SafeEncoder.encode(String.valueOf(this.getLifeTime()))};
		return Mono.from(ReactiveOperations.runScript(script, ScriptOutputType.VALUE, values));
	}

	@Override
	public Mono<Boolean> pub(String key, String k2, T v) {
		if (this.isValid()) {
			return _hput(key, k2, v);
		}
		return ReactiveOperations.hSet(key, k2, this.ser.serialize(v));
	}
	
	/**
	 * 高性能put
	 * @param key
	 * @return
	 */
	protected Mono<Boolean> _hput(String key, String k2, T v) {
		String script = Cons.MAP_PUT_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(this.getKeyName(key)), SafeEncoder.encode(k2), this.ser.serialize(v), SafeEncoder.encode(String.valueOf(this.getLifeTime()))};
		return Mono.from(ReactiveOperations.runScript(script, ScriptOutputType.VALUE, values));
	}

	@Override
	public Mono<Long> delete(String key, String k2) {
		return ReactiveOperations.hDel(this.getKeyName(key), k2);
	}

	@Override
	public ReactiveMultiMap<String, T> expire(int seconds) {
		this.lifeTime = seconds;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReactiveMultiMap<String, String> primitive() {
		this.setStrategy(new PrimitiveStrategy());
		return (ReactiveMultiMap<String, String>) this;
	}

	@Override
	public ReactiveMultiMap<String, T> complex() {
		this.setStrategy(new ComplexStrategy());
		return this;
	}

}
