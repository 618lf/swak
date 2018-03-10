package com.swak.common.cache.redis.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.swak.common.serializer.SerializeException;
import com.swak.common.utils.Lists;
import com.swak.common.utils.Maps;
import com.swak.common.utils.Sets;
import com.swak.common.utils.StringUtils;

import net.sf.ehcache.CacheException;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.util.SafeEncoder;

/**
 * 单个redis 服务器
 * KEY 的统一编码是 ISO-8859-1
 * @author root
 */
public class RedisCacheUtils implements IRedisCacheUtils{
	
	// 连接池
	private static JedisPool pool;
	
	public RedisCacheUtils(JedisPoolConfig poolConfig, String hosts, String password, int timeout) {
		String host = null; int port = 0;
		hosts = hosts.replaceAll("\\s", "");
		String[] hostPorts = hosts.split(",");
		for(String hostPort : hostPorts) {
			String[] hostPortArr = hostPort.split(":");
			host = hostPortArr[0];
			port = Integer.valueOf(hostPortArr[1]);
			if(!host.isEmpty() && port != 0) {
				break;
			}
		}
		if (password == null || password == "" || password.isEmpty()) {
			pool = new JedisPool(poolConfig, host, port, timeout);
		} else {
			pool = new JedisPool(poolConfig, host, port, timeout, password);
		}
	}
     
	/**
	 * 订阅
	 */
	@Override
	public void subscribe(BinaryJedisPubSub o, byte[] channels) {
		Jedis jedis = this.getJedis();
		jedis.subscribe(o, channels);
		jedis.close();
	}
	
	/**
	 * 订阅
	 */
	@Override
	public void publish(byte[] channel, byte[] message) {
		Jedis jedis = this.getJedis();
		jedis.publish(channel, message);
		jedis.close();
	}

	/**
	 * 得到链接
	 * @return
	 */
	public Jedis getJedis() {
		return pool.getResource();
	}
	
	/**
	 * 得到链接
	 * @return
	 */
	public void release(Jedis jedis) {
		if (jedis != null) { 
			jedis.close();
		}
	}

	@Override
	public byte[] get(String key) {
		if(key == null) return null;
		Jedis jedis = this.getJedis();
		try{
			return jedis.get(SafeEncoder.encode(key));
		}catch(SerializeException e){
			this.innerDel(jedis, key);
			return null;
	    }catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	private void innerDel(Jedis jedis, Object key) {
		try {
			if( key != null && key instanceof String) {
				jedis.del((String) key);
			} else if(key != null && key instanceof byte[]) {
				jedis.del((byte[]) key);
			} else if(key != null && key instanceof String[]) {
				jedis.del((String[]) key);
			}
		} catch (Exception e) {}
	}

	@Override
	public Long delete(String... keys) {
		if(keys == null) return 0L;
		Jedis jedis = this.getJedis();
		try{
			return jedis.del(keys);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public Long delete(String key) {
		if(key == null) return null;
		Jedis jedis = this.getJedis();
		try{
			return jedis.del(SafeEncoder.encode(key));
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	/**
	 * 根据规则删除数据
	 */
	@Override
	public void deletes(String pattern) {
		if(pattern == null) return;
		final Jedis jedis = this.getJedis();
		try{
			ScanCallback callback = new ScanCallback(){
				@Override
				public boolean success(List<String> keys) {
					for (String key: keys) {
						jedis.del(key);
					}
					return true;
				}
			};
			// 执行的条件
			ScanParams params = new ScanParams().match(pattern).count(100);
			this.scan(pattern, params, callback);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	/**
	 * 最多只取 100 条数据
	 */
	@Override
	public Set<String> keys(String pattern) {
		if(pattern == null) return null;
		Jedis jedis = this.getJedis();
		try{
			final Set<String> keys = Sets.newHashSet();
			
			/**
			 * 查询回调
			 */
			ScanCallback callback = new ScanCallback(){

				@Override
				public boolean success(List<String> _keys) {
					keys.addAll(_keys);
					if (keys.size() >= 100) {
						return false;
					}
					return true;
				}
				
			};
			// 执行的条件
			ScanParams params = new ScanParams().match(pattern).count(100);
			this.scan(pattern, params, callback);
			return keys;
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	@Override
	public List<byte[]> gets(final String pattern) {
		if(pattern == null) return null;
		final Jedis jedis = this.getJedis();
		try{
			
            final List<byte[]> values = new ArrayList<byte[]>();
			
			/**
			 * 查询回调
			 */
			ScanCallback callback = new ScanCallback(){

				@Override
				public boolean success(List<String> keys) {
					for(String key: keys) {
						values.add(jedis.get(SafeEncoder.encode(key)));
					}
					if (values.size() >= 100) {
						return false;
					}
					return true;
				}
				
			};
			// 执行的条件
			ScanParams params = new ScanParams().match(pattern).count(100);
			this.scan(pattern, params, callback);
			return values;
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public Long expire(String key, int seconds) {
		if(key == null) return null;
		Jedis jedis = this.getJedis();
		try{
			return jedis.expire(SafeEncoder.encode(key), seconds);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public boolean exists(String key) {
		if(key == null) return false;
		Jedis jedis = this.getJedis();
		try{
			return jedis.exists(key);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	@Override
	public Map<String, byte[]> keyValues(final String pattern, final String prex) {
		if(pattern == null) return null;
		final Jedis jedis = this.getJedis();
		try{
			
			final Map<String,byte[]> values = Maps.newHashMap();
			
			/**
			 * 查询回调
			 */
			ScanCallback callback = new ScanCallback(){

				@Override
				public boolean success(List<String> keys) {
					for(String key: keys) {
						values.put(StringUtils.removeStart(key, prex), jedis.get(SafeEncoder.encode(key)));
					}
					if (values.size() >= 100) {
						return false;
					}
					return true;
				}
				
			};
			// 执行的条件
			ScanParams params = new ScanParams().match(pattern).count(100);
			this.scan(pattern, params, callback);
			return values;
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	@Override
	public long size(String pattern) {
		if(pattern == null) return 0;
		final Jedis jedis = this.getJedis();
		try{
			
			final AtomicLong size = new AtomicLong(0);
			
			/**
			 * 查询回调
			 */
			ScanCallback callback = new ScanCallback(){

				@Override
				public boolean success(List<String> keys) {
					long _size = size.get();
					size.set(_size + keys.size());
					return true;
				}
				
			};
			// 执行的条件
			ScanParams params = new ScanParams().match(pattern).count(100);
			this.scan(pattern, params, callback);
			return size.get();
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	/**
	 * 执行查询
	 */
	@Override
	public void scan(String pattern, ScanParams params, ScanCallback callback) {
		if(pattern == null) return;
		Jedis jedis = this.getJedis();
		try{
			String cursor = "0"; String nextCursor = cursor;
			ScanResult<String> result = null;
			do {
				// 查询数据
				result = jedis.scan(nextCursor, params);
				if (result == null) {break;}
				
				// 实际的业务逻辑
				List<String> _keys = result.getResult();
				if (_keys.isEmpty()) {break;}
				boolean con = callback.success(_keys);
				if (!con) {break;}
			} while(!cursor.equals(nextCursor = result.getStringCursor()));
		}catch(Exception e){
			throw new CacheException(e);
		}
	}

	// hash 操作
	@Override
	public Long hDel(String key, String... fields) {
		if(key == null || fields == null || fields.length == 0) return 0l;
		Jedis jedis = this.getJedis();
		try{
			return jedis.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields));
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public Boolean hExists(String key, String field) {
		if(key == null || field == null) return false;
		Jedis jedis = this.getJedis();
		try{
			return jedis.hexists(SafeEncoder.encode(key), SafeEncoder.encode(field));
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public byte[] hGet(String key, String field) {
		if(key == null || field == null) return null;
		Jedis jedis = this.getJedis();
		try{
			return jedis.hget(SafeEncoder.encode(key), SafeEncoder.encode(field));
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public Map<String, byte[]> hGetAll(String key) {
		if(key == null) return null;
		Jedis jedis = this.getJedis();
		try{
			Map<String, byte[]> values = Maps.newHashMap();
			Map<byte[], byte[]> maps = jedis.hgetAll(SafeEncoder.encode(key));
			Iterator<byte[]> it = maps.keySet().iterator();
			while(it.hasNext()) {
				byte[] _key = it.next();
				byte[] _val = maps.get(_key);
				values.put(SafeEncoder.encode(_key), _val);
			}
			return values;
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public Set<String> hKeys(String key) {
		if(key == null) return null;
		Jedis jedis = this.getJedis();
		try{
			return jedis.hkeys(key);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public Long hLen(String key) {
		if(key == null) return 0L;
		Jedis jedis = this.getJedis();
		try{
			return jedis.hlen(key);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public List<byte[]> hMGet(String key, String... fields) {
		if(key == null || fields == null || fields.length == 0) return null;
		Jedis jedis = this.getJedis();
		try{
			return jedis.hmget(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields));
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public void hMSet(String key, Map<String, byte[]> tuple) {
		if(key == null || tuple == null || tuple.isEmpty()) return;
		Jedis jedis = this.getJedis();
		try{
			List<byte[]> dels = Lists.newArrayList();
			Map<byte[], byte[]> maps = Maps.newHashMap();
			Iterator<String> it = tuple.keySet().iterator();
			while(it.hasNext()) {
				String _key = it.next();
				byte[] _val = tuple.get(_key);
				if (_val == null) {
					dels.add(SafeEncoder.encode(_key));
				} else {
					maps.put(SafeEncoder.encode(_key), _val);
				}
			}
			if (dels.size() != 0) {
				byte[][] _dels = new byte[dels.size()][]; 
				_dels = dels.toArray(_dels);
				jedis.hdel(SafeEncoder.encode(key), _dels);
			}
			jedis.hmset(SafeEncoder.encode(key), maps);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public List<byte[]> hVals(String key) {
		if(key == null) return null;
		Jedis jedis = this.getJedis();
		try{
			return jedis.hvals(SafeEncoder.encode(key));
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	// 一组原始的操作 单key是String
	@Override
	public void add(String key, byte[] value) {
		if(value == null || key == null) { return; }
		Jedis jedis = this.getJedis();
		try{
			jedis.setnx(SafeEncoder.encode(key), value);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public void set(String key, byte[] value) {
		if(value == null || key == null) { return; }
		Jedis jedis = this.getJedis();
		try{
			jedis.set(SafeEncoder.encode(key), value);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public void set(String key, byte[] value, int seconds) {
		if(value == null || key == null) { return; }
		Jedis jedis = this.getJedis();
		try{
			jedis.setex(SafeEncoder.encode(key), seconds, value);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public Boolean hSet(String key, String field, byte[] value) {
		if(key == null || field == null) return false;
		Jedis jedis = this.getJedis();
		try{
			Long result = jedis.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value);
			return result != null ? result == 1 : null;
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	public Boolean hSetNX(String key, String field, byte[] value) {
		if(key == null || field == null) return false;
		Jedis jedis = this.getJedis();
		try{
			Long result = jedis.hsetnx(SafeEncoder.encode(key), SafeEncoder.encode(field), value);
			return result != null ? result == 1 : null;
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	/**
	 * 一次执行
	 */
	@Override
	public byte[] invoke(Callback call) {
		Jedis jedis = this.getJedis();
		try {
			return call.doCall(jedis);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	@Override
	public long ttl(String key) {
		Jedis jedis = this.getJedis();
		try {
			return jedis.ttl(key.toString());
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	@Override
	public long lLen(String key) {
		Jedis jedis = this.getJedis();
		try {
			return jedis.llen(key);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	@Override
	public long lPush(String key, byte[] ... value) {
		Jedis jedis = this.getJedis();
		try {
			return jedis.lpush(SafeEncoder.encode(key), value);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	@Override
	public byte[] lPop(String key) {
		Jedis jedis = this.getJedis();
		try {
			return jedis.lpop(SafeEncoder.encode(key));
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	@Override
	public long rPush(String key, byte[] ... value) {
		Jedis jedis = this.getJedis();
		try {
			return jedis.rpush(SafeEncoder.encode(key), value);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
	
	@Override
	public byte[] rPop(String key) {
		Jedis jedis = this.getJedis();
		try {
			return jedis.rpop(SafeEncoder.encode(key));
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	// script 脚本执行能力
	@Override
	public void run(String script, byte[] ... keys) {
		Jedis jedis = this.getJedis();
		try {
			jedis.eval(SafeEncoder.encode(script), SafeEncoder.encode(String.valueOf(keys.length)), keys);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T runAndGetOne(String script, byte[] ... keys) {
		Jedis jedis = this.getJedis();
		try {
			return (T)jedis.eval(SafeEncoder.encode(script), SafeEncoder.encode(String.valueOf(keys.length)), keys);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> runAndGetList(String script, byte[] ... keys) {
		Jedis jedis = this.getJedis();
		try {
			return (List<T>) jedis.eval(SafeEncoder.encode(script), SafeEncoder.encode(String.valueOf(keys.length)), keys);
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			this.release(jedis);
		}
	}
}