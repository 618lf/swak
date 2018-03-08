package com.swak.common.cache.redis.factory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.swak.common.serializer.SerializationUtils;
import com.swak.common.utils.Lists;
import com.swak.common.utils.Maps;
import com.swak.common.utils.Sets;
import com.swak.common.utils.StringUtils;

import net.sf.ehcache.CacheException;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.util.SafeEncoder;

/**
 * redis 集群
 * @author root
 */
public class RedisClusterCacheUtils implements IRedisCacheUtils{

	private static JedisCluster jedisCluster;
	private static int numRetries = 30;
	
	public RedisClusterCacheUtils(JedisPoolConfig poolConfig, String hosts, String password, int timeout) {
		if (!StringUtils.hasText(password)) {
			jedisCluster = new JedisCluster(getJedisClusterNodesSet(hosts), timeout, poolConfig);
		} else {
			// 使用了默认的 JedisCluster.DEFAULT_MAX_REDIRECTIONS
			jedisCluster = new JedisCluster(getJedisClusterNodesSet(hosts), timeout, timeout, 5, password, poolConfig);
		}
	}
	
	private Set<HostAndPort> getJedisClusterNodesSet(String hosts) {
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		hosts = hosts.replaceAll("\\s", "");
		String[] hostPorts = hosts.split(",");
		for(String hostPort : hostPorts) {
			String[] hostPortArr = hostPort.split(":");
			nodes.add(new HostAndPort(hostPortArr[0], Integer.valueOf(hostPortArr[1])));
		}
		return nodes;
	}
	
	/**
	 * 订阅
	 */
	@Override
	public void subscribe(BinaryJedisPubSub o, byte[] channels) {
		JedisCluster jedis = this.getJedis();
		jedis.subscribe(o, channels);
	}
	
	/**
	 * 发布
	 */
	@Override
	public void publish(byte[] channel, byte[] message) {
		JedisCluster jedis = this.getJedis();
		jedis.publish(channel, message);
	}

	/**
	 * 得到链接
	 * @return
	 */
	public JedisCluster getJedis() {
		return jedisCluster;
	}
	
	/**
	 * 等待失败处理
	 */
	private void waitforFailover() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
	
	@Override
	public void add(String key, Object value) {
		if(value == null || key == null) { return; }
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		do {
			try{
				jedis.setnx(SafeEncoder.encode(key), SerializationUtils.serialize(value));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
	}

	@Override
	public void set(String key, Object value) {
		if(value == null || key == null) { return; }
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		do {
			try{
				jedis.set(SafeEncoder.encode(key), SerializationUtils.serialize(value));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
	}

	@Override
	public void set(String key, Object value, int seconds) {
		if(value == null || key == null) { return; }
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		do {
			try{
				jedis.setex(SafeEncoder.encode(key), seconds, SerializationUtils.serialize(value));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(String key) {
		if(key == null) {return null;}
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		T value = null;
		do {
			try{
				value = (T)SerializationUtils.deserialize(jedis.get(SafeEncoder.encode(key)));
				sucess = true;
			}catch(Exception e){
				this.innerDel(jedis, key);
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return value;
	}
	
	private void innerDel(JedisCluster jedis, Object key) {
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
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Long value = 0L;
		do {
			try{
				value = jedis.del(keys);
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return value;
	}

	@Override
	public Long delete(String key) {
		if(key == null) return 0L;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Long value = 0L;
		do {
			try{
				value = jedis.del(SafeEncoder.encode(key));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return value;
	}
	
	@Override
	public void deletes(String pattern) {
		if(pattern == null) return;
		int tries = 0; boolean sucess = false;
		final JedisCluster jedis = this.getJedis();
		do {
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
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
	}

	/**
	 * 有疑问
	 */
	@Override
	public Set<String> keys(final String pattern) {
		if(pattern == null) return null;
		int tries = 0; boolean sucess = false;
		final Set<String> keys = Sets.newHashSet();;
		do {
			try{
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
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return keys;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> valueOfObjects(final String pattern) {
		if(pattern == null) return null;
		int tries = 0; boolean sucess = false;
		final JedisCluster jedis = this.getJedis();
		final List<T> values = Lists.newArrayList();
		do {
			try{
				/**
				 * 查询回调
				 */
				ScanCallback callback = new ScanCallback(){

					@Override
					public boolean success(List<String> keys) {
						for(String key: keys) {
							T value = (T)SerializationUtils.deserialize(jedis.get(SafeEncoder.encode(key)));
							values.add(value);
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
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return values;
	}

	@Override
	public Long expire(String key, int seconds) {
		if(key == null) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Long value = 0L;
		do {
			try{
				value = jedis.expire(SafeEncoder.encode(key), seconds);
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return value;
	}

	@Override
	public boolean exists(Object key) {
		if(key == null) return false;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		boolean value = false;
		do {
			try{
				value = jedis.exists(String.valueOf(key));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<Object, T> keyValues(final String pattern, final String prex) {
		if(pattern == null) return null;
		int tries = 0; boolean sucess = false;
		final JedisCluster jedis = this.getJedis();
		final Map<Object, T> values = Maps.newHashMap();
		do {
			try{
				
				/**
				 * 查询回调
				 */
				ScanCallback callback = new ScanCallback(){

					@Override
					public boolean success(List<String> keys) {
						for(String key: keys) {
							T value = (T)SerializationUtils.deserialize(jedis.get(SafeEncoder.encode(key)));
							values.put(StringUtils.removeStart(key, prex), value);
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
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return values;
	}
	

	@Override
	public long size(String pattern) {
		if(pattern == null) return 0;
		int tries = 0; boolean sucess = false;
		final AtomicLong size = new AtomicLong(0);
		do {
			try{
				
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
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return size.get();
	}

	/**
	 * 循环遍历每个节点
	 * @param pattern
	 * @param params
	 * @param callback
	 */
	@Override
	public void scan(String pattern, ScanParams params, ScanCallback callback) {
		Collection<JedisPool> clusterNodes = jedisCluster.getClusterNodes().values();
		for(JedisPool pool: clusterNodes) {
			Jedis jedis = pool.getResource();
			boolean con = this.doScan(jedis, pattern, params, callback);
			if (!con) {return;}
		}
	}
	
	/**
	 * 具体的执行
	 * @param jedis
	 * @param pattern
	 * @param params
	 * @param callback
	 */
	private boolean doScan(Jedis jedis, String pattern, ScanParams params, ScanCallback callback) {
		if(pattern == null) return false;
		try{
			String cursor = "0"; String nextCursor = cursor;
			ScanResult<String> result = null;
			while ((result = jedis.scan(nextCursor, params)) != null 
					&& !cursor.equals(nextCursor = result.getStringCursor())) {
				List<String> _keys = result.getResult();
				if (_keys.isEmpty()) {break;}
				boolean con = callback.success(_keys);
				if (!con) {
					
					// 客户端提示不需要执行了
					return false;
				}
			}
			return true;
		}catch(Exception e){
			throw new CacheException(e);
		}finally{
			jedis.close();
		}
	}

	// hash 操作
	@Override
	public Boolean hSet(String key, String field, Object value) {
		if(key == null || field == null) return false;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Boolean falg = null;
		do {
			try{
				Long result = jedis.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), SerializationUtils.serialize(value));
				falg = result != null ? result == 1 : null;
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return falg;
	}

	@Override
	public Boolean hSetNX(String key, String field, Object value) {
		if(key == null || field == null) return false;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Boolean falg = null;
		do {
			try{
				Long result = jedis.hsetnx(SafeEncoder.encode(key), SafeEncoder.encode(field), SerializationUtils.serialize(value));
				falg = result != null ? result == 1 : null;
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return falg;
	}

	@Override
	public Long hDel(String key, String... fields) {
		if(key == null || fields == null || fields.length == 0) return 0l;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Long szie = null;
		do {
			try{
				szie = jedis.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return szie;
	}

	@Override
	public Boolean hExists(String key, String field) {
		if(key == null || field == null) return false;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Boolean exists = null;
		do {
			try{
				exists = jedis.hexists(SafeEncoder.encode(key), SafeEncoder.encode(field));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return exists;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T hGet(String key, String field) {
		if(key == null || field == null) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		T t = null;
		do {
			try{
				t = (T)SerializationUtils.deserialize(jedis.hget(SafeEncoder.encode(key), SafeEncoder.encode(field)));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return t;
	}

	@Override
	public Map<String, Object> hGetAll(String key) {
		if(key == null) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Map<String, Object> values = Maps.newHashMap();
		do {
			try{
				Map<byte[], byte[]> maps = jedis.hgetAll(SafeEncoder.encode(key));
				Iterator<byte[]> it = maps.keySet().iterator();
				while(it.hasNext()) {
					byte[] _key = it.next();
					byte[] _val = maps.get(_key);
					values.put(SafeEncoder.encode(_key), SerializationUtils.deserialize(_val));
				}
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return values;
	}

	@Override
	public Set<String> hKeys(String key) {
		if(key == null) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Set<String> keys = null;
		do {
			try{
				keys = jedis.hkeys(key);
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return keys;
	}

	@Override
	public Long hLen(String key) {
		if(key == null) return 0L;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Long size = 0L;
		do {
			try{
				size = jedis.hlen(key);
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return size;
	}

	@Override
	public List<Object> hMGet(String key, String... fields) {
		if(key == null || fields == null || fields.length == 0) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		List<Object> vals = Lists.newArrayList();
		do {
			try{
				List<byte[]> values = jedis.hmget(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields));
				for(byte[] val: values) {
					vals.add(SerializationUtils.deserialize(val));
				}
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return vals;
	}

	@Override
	public void hMSet(String key, Map<String, Object> tuple) {
		if(key == null || tuple == null || tuple.isEmpty()) return;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		do {
			try{
				Map<byte[], byte[]> maps = Maps.newHashMap();
				Iterator<String> it = tuple.keySet().iterator();
				while(it.hasNext()) {
					String _key = it.next();
					Object _val = tuple.get(_key);
					maps.put(SafeEncoder.encode(_key), SerializationUtils.serialize(_val));
				}
				jedis.hmset(SafeEncoder.encode(key), maps);
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
	}

	@Override
	public List<Object> hVals(String key) {
		if(key == null) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		List<Object> vals = Lists.newArrayList();
		do {
			try{
				Collection<byte[]> values = jedis.hvals(SafeEncoder.encode(key));
				for(byte[] val: values) {
					vals.add(SerializationUtils.deserialize(val));
				}
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return vals;
	}

	// 一组原始的操作 单key是String
	@Override
	public void add(String key, byte[] value) {
		if(value == null || key == null) { return; }
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		do {
			try{
				jedis.setnx(SafeEncoder.encode(key), value);
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		
	}

	@Override
	public void set(String key, byte[] value) {
		if(value == null || key == null) { return; }
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		do {
			try{
				jedis.set(SafeEncoder.encode(key), value);
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
	}

	@Override
	public void set(String key, byte[] value, int seconds) {
		if(value == null || key == null) { return; }
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		do {
			try{
				jedis.setex(SafeEncoder.encode(key), seconds, value);
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
	}

	@Override
	public Boolean hSet(String key, String field, byte[] value) {
		if(key == null || field == null) return false;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Boolean falg = null;
		do {
			try{
				Long result = jedis.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value);
				falg = result != null ? result == 1 : null;
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return falg;
	}

	@Override
	public Boolean hSetNX(String key, String field, byte[] value) {
		if(key == null || field == null) return false;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Boolean falg = null;
		do {
			try{
				Long result = jedis.hsetnx(SafeEncoder.encode(key), SafeEncoder.encode(field), value);
				falg = result != null ? result == 1 : null;
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return falg;
	}

	@Override
	public byte[] hGet2(String key, String field) {
		if(key == null || field == null) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		byte[] t = null;
		do {
			try{
				t = jedis.hget(SafeEncoder.encode(key), SafeEncoder.encode(field));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return t;
	}

	@Override
	public Map<String, byte[]> hGetAll2(String key) {
		if(key == null) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		Map<String, byte[]> values = Maps.newHashMap();
		do {
			try{
				Map<byte[], byte[]> maps = jedis.hgetAll(SafeEncoder.encode(key));
				Iterator<byte[]> it = maps.keySet().iterator();
				while(it.hasNext()) {
					byte[] _key = it.next();
					byte[] _val = maps.get(_key);
					values.put(SafeEncoder.encode(_key), _val);
				}
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return values;
	}

	@Override
	public List<byte[]> hMGet2(String key, String... fields) {
		if(key == null || fields == null || fields.length == 0) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		List<byte[]> vals = null;
		do {
			try{
				vals = jedis.hmget(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return vals;
	}

	@Override
	public void hMSet2(String key, Map<String, byte[]> tuple) {
		if(key == null || tuple == null || tuple.isEmpty()) return;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		do {
			try{
				Map<byte[], byte[]> maps = Maps.newHashMap();
				Iterator<String> it = tuple.keySet().iterator();
				while(it.hasNext()) {
					String _key = it.next();
					byte[] _val = tuple.get(_key);
					maps.put(SafeEncoder.encode(_key), _val);
				}
				jedis.hmset(SafeEncoder.encode(key), maps);
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
	}

	@Override
	public List<byte[]> hVals2(String key) {
		if(key == null) return null;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		List<byte[]> vals = null;
		do {
			try{
				vals = Lists.newArrayList(jedis.hvals(SafeEncoder.encode(key)));
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return vals;
	}

	/**
	 * 一次执行(集群中需要找到key对应的节点)
	 */
	@Override
	public <T> T invoke(Callback<T> call) {
		return null;
	}

	@Override
	public long ttl(Object key) {
		if(key == null) return -2;
		int tries = 0; boolean sucess = false;
		JedisCluster jedis = this.getJedis();
		long vals = -2;
		do {
			try{
				vals = jedis.ttl(key.toString());
				sucess = true;
			}catch(Exception e){
				waitforFailover();
			}
		} while (!sucess && tries <= numRetries);
		return vals;
	}
	
	// list

	@Override
	public long lPush(String key, Object... value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> T lPop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long rPush(String key, Object... value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> T rPop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run(String script, String... keys) {
		
	}

	@Override
	public <T> T runAndGetOne(String script, String... keys) {
		return null;
	}

	@Override
	public <T> List<T> runAndGetList(String script, String... keys) {
		return null;
	}
}