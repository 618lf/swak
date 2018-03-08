package com.swak.common.cache.redis.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;

/**
 * 接口实现，底层可以是单个redis缓存也可以是集群
 * @author root
 */
public interface IRedisCacheUtils {
   	
	/**
	 * 订阅
	 * @param o
	 * @param channels
	 */
	public void subscribe(BinaryJedisPubSub o, byte[] channels);
	
	/**
	 * 发布
	 * @param channel
	 * @param message
	 */
	public void publish(byte[] channel, byte[] message);
	
	/**
	 * 添加-- 不存在才添加
	 * @param key
	 * @param value
	 */
	public void add(final String key, Object value);
	
	/**
	 * 不管是否存在都添加会覆盖之前的数据
	 * @param key
	 * @param value
	 */
	public void set(final String key, Object value);
	
	/**
	 * 存储一个值, 并设置过期时间
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public void set(final String key, Object value, int seconds);
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	public <T> T getObject(String key);
	
	/**
	 * 删除一组值
	 * @param keys
	 * @return
	 */
	public Long delete(String ...keys );
	
	/**
	 * 删除一个值
	 * @param key
	 * @return
	 */
	public Long delete(String key);
	
	/**
	 * 删除一个值
	 * @param key
	 * @return
	 */
	public void deletes(String pattern);
	
	/**
	 * 总数
	 * @param key
	 * @return
	 */
	public long size(String pattern);
	
	/**
	 * 所有的key
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(final String pattern);
	
	/**
	 * 对象列表
	 * @param pattern
	 * @return
	 */
	public <T> List<T> valueOfObjects(final String pattern);
	
	/**
	 * ttl
	 * @param key
	 * @param seconds
	 * @return
	 */
	public Long expire(final String key, final int seconds);
	
	/**
	 * 判断是否存在
	 * @param key
	 * @return
	 */
	public boolean exists(Object key);
	
	/**
	 * 判断是否存在
	 * @param key
	 * @return
	 */
	public long ttl(Object key);
	
	/**
	 * key + value
	 * @param pattern
	 * @param prex
	 * @return
	 */
	public <T> Map<Object,T> keyValues(final String pattern, final String prex);
	
	/**
	 * 查找
	 */
	public void scan(final String pattern, ScanParams params, ScanCallback callback);
	
	/**
	 * 查询结果
	 * @author lifeng
	 */
	public interface ScanCallback {
		
		/**
		 * 执行成功 -- 查询的keys
		 * @return -- 是否继续执行
		 */
		public boolean success(List<String> keys);
	}
	
	// hash  操作
	public Boolean hSet(String key, String field, Object value);
	public Boolean hSetNX(String key, String field, Object value);
	public Long hDel(String key, String... fields);
	public Boolean hExists(String key, String field);
	public <T> T hGet(String key, String field);
	public Map<String, Object> hGetAll(String key);
	public Set<String> hKeys(String key);
	public Long hLen(String key);
	public List<Object> hMGet(String key, String... fields);
	public void hMSet(String key, Map<String, Object> tuple);
	public List<Object> hVals(String key);
	
	// list 操作
	public long lPush(String key, Object ... value);
	public <T> T lPop(String key);
	public long rPush(String key, Object ... value);
	public <T> T rPop(String key);
	
	// 一组原始的操作
	public void add(final String key, byte[] value);
	public void set(final String key, byte[] value);
	public void set(final String key, byte[] value, int seconds);
	public Boolean hSet(String key, String field, byte[] value);
	public Boolean hSetNX(String key, String field, byte[] value);
	public byte[] hGet2(String key, String field);
	public Map<String, byte[]> hGetAll2(String key);
	public List<byte[]> hMGet2(String key, String... fields);
	public void hMSet2(String key, Map<String, byte[]> tuple);
	public List<byte[]> hVals2(String key);
	
	// 脚本执行能力
	public void run(String script, String ... keys);
	public <T> T runAndGetOne(String script, String ... keys);
	public <T> List<T> runAndGetList(String script, String ... keys);
	
	// 在一个 jedis 中执行
	public <T> T invoke(Callback<T> call);
	
	/**
	 * 回调
	 * @author lifeng
	 *
	 * @param <T>
	 */
	public interface Callback<T> {
		
		/**
		 * 执行回调 -- 只能对应到一个Jedis
		 * @param jedis
		 * @return
		 */
        T doCall(Jedis jedis);
    }
}