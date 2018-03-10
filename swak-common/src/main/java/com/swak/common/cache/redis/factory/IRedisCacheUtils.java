package com.swak.common.cache.redis.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;

/**
 * 定义接口，redis 层面全部
 * 使用 String <-> byte[] 规格
 * @author root
 */
public interface IRedisCacheUtils {
   	
	public void subscribe(BinaryJedisPubSub o, byte[] channels);
	public void publish(byte[] channel, byte[] message);
	public void add(final String key, byte[] value);
	public void set(final String key, byte[] value);
	public void set(final String key, byte[] value, int seconds);
	public byte[] get(String key);
	public Long delete(String ... keys);
	public Long delete(String key);
	public void deletes(String pattern);
	public long size(String pattern);
	public Set<String> keys(final String pattern);
	public List<byte[]> gets(final String pattern);
	public Long expire(final String key, final int seconds);
	public boolean exists(String key);
	public Map<String, byte[]> keyValues(final String pattern, final String prex);
	public long ttl(String key);
	public void scan(final String pattern, ScanParams params, ScanCallback callback);
	
	public Boolean hSet(String key, String field, byte[] value);
	public Boolean hSetNX(String key, String field, byte[] value);
	public Long hDel(String key, String... fields);
	public Boolean hExists(String key, String field);
	public byte[] hGet(String key, String field);
	public Map<String, byte[]> hGetAll(String key);
	public Set<String> hKeys(String key);
	public Long hLen(String key);
	public List<byte[]> hMGet(String key, String... fields);
	public void hMSet(String key, Map<String, byte[]> tuple);
	public List<byte[]> hVals(String key);
	
	public long lPush(String key, byte[] ... value);
	public byte[] lPop(String key);
	public long rPush(String key, byte[] ... value);
	public byte[] rPop(String key);
	public long lLen(String key);
	
	public void run(String script, byte[] ... values);
	public <T> T runAndGetOne(String script, byte[] ... values);
	public <T> List<T> runAndGetList(String script, byte[] ... values);
	public byte[] invoke(Callback call);
	
	public interface Callback {
		byte[] doCall(Jedis jedis);
    }
	public interface ScanCallback {
		public boolean success(List<String> keys);
	}
}