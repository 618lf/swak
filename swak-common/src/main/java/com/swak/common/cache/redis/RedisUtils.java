package com.swak.common.cache.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.swak.common.cache.redis.factory.IRedisCacheUtils.Callback;
import com.swak.common.cache.redis.factory.RedisConnectionFactory;
import com.swak.common.utils.SpringContextHolder;

import redis.clients.jedis.BinaryJedisPubSub;


/**
 * redis 的简单使用
 * 如果全局使用，有两种方案（使用前缀，list 等集合）:先使用前缀的方式
 * 存储字节或字符，请使用一种
 * @author lifeng
 */
public class RedisUtils {
	
	//工厂
	private static RedisConnectionFactory factory = SpringContextHolder.getBean(RedisConnectionFactory.class);
	
	/**
	 * 不存在才添加
	 * @param key
	 * @param value
	 */
	public static void add(final String key, Object value){
		factory.getRedisCache().add(key, value);
	}
	
	/**
	 * 存储一个值
	 * @param key
	 * @param value
	 */
	public static void set(final String key, Object value){
		factory.getRedisCache().set(key, value);
	}
	
	/**
	 * 存储一个值和过期时间  set + expire
	 * @param key
	 * @param value
	 */
	public static void set(final String key, Object value, int seconds){
		factory.getRedisCache().set(key, value, seconds);
	}
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	public static <T> T getObject(String key) {
		return factory.getRedisCache().getObject(key);
	}
	
	/**
	 * 删除给定的keys
	 * @param keys
	 * @return
	 */
	public static Long delete(String ...keys){
		return factory.getRedisCache().delete(keys);
	}
	
	/**
	 * 删除一个值
	 * @param key
	 * @return
	 */
	public static Long delete(String key) {
		return factory.getRedisCache().delete(key);
	}
	
	/**
	 * 删除查询到的所有的数据
	 * @param pattern
	 */
	public static void deletes(String pattern) {
		factory.getRedisCache().deletes(pattern);
	}
	
	/**
	 * 总数
	 * @param pattern
	 * @return
	 */
	public static long size(String pattern) {
		return factory.getRedisCache().size(pattern);
	}
	
	/**
	 * 返回key 的集合
	 * @param pattern
	 * @return
	 */
	public static Set<String> keys(final String pattern) {
		return factory.getRedisCache().keys(pattern);
	}
	
	/**
	 * 返回对象List
	 * @param pattern
	 * @return
	 */
	public static <T> List<T> valueOfObjects(final String pattern){
		return factory.getRedisCache().valueOfObjects(pattern);
	}
	
	/**
	 * 设置某个key的过期时间
	 * @param key
	 * @param seconds
	 * @return
	 */
	public static Long expire(final String key, final int seconds) {
		return factory.getRedisCache().expire(key, seconds);
	}
	
	/**
	 * 返回当前key是否存在
	 * @param key
	 * @return
	 */
	public static boolean exists(Object key) {
		return factory.getRedisCache().exists(key);
	}
	
	/**
	 * 返回对象List
	 * @param pattern
	 * @return
	 */
	public static <T> Map<Object,T> keyValues(final String pattern, final String prex){
		return factory.getRedisCache().keyValues(pattern, prex);
	}
	
	/**
	 * 订阅
	 * @param o
	 * @param channels
	 */
	public static void subscribe(BinaryJedisPubSub o, byte[] channels) {
		factory.getRedisCache().subscribe(o, channels);
	}
	
	/**
	 * 发布消息
	 * @param channel
	 * @param message
	 */
	public static void publish(byte[] channel, byte[] message) {
		factory.getRedisCache().publish(channel, message);
	}
	
	// hash  操作
	public static Boolean hSet(String key, String field, Object value){
		return factory.getRedisCache().hSet(key, field, value);
	}
	public static Boolean hSetNX(String key, String field, Object value){
		return factory.getRedisCache().hSetNX(key, field, value);
	}
	public static Long hDel(String key, String... fields) {
		return factory.getRedisCache().hDel(key, fields);
	}
	public static Boolean hExists(String key, String field) {
		return factory.getRedisCache().hExists(key, field);
	}
	public static <T> T hGet(String key, String field) {
		return factory.getRedisCache().hGet(key, field);
	}
	public static Map<String, Object> hGetAll(String key) {
		return factory.getRedisCache().hGetAll(key);
	}
	public static Set<String> hKeys(String key) {
		return factory.getRedisCache().hKeys(key);
	}
	public static Long hLen(String key) {
		return factory.getRedisCache().hLen(key);
	}
	public static List<Object> hMGet(String key, String... fields) {
		return factory.getRedisCache().hMGet(key, fields);
	}
	public static void hMSet(String key, Map<String, Object> tuple) {
		factory.getRedisCache().hMSet(key, tuple);
	}
	public static List<Object> hVals(String key) {
		return factory.getRedisCache().hVals(key);
	}
	
	// 一组原始的操作
	public static void add(final String key, byte[] value) {
		factory.getRedisCache().add(key, value);
	}
	public static void set(final String key, byte[] value) {
		factory.getRedisCache().set(key, value);
	}
	public static void set(final String key, byte[] value, int seconds) {
		factory.getRedisCache().set(key, value, seconds);
	}
	public static Boolean hSet(String key, String field, byte[] value) {
		return factory.getRedisCache().hSet(key, field, value);
	}
	public static Boolean hSetNX(String key, String field, byte[] value) {
		return factory.getRedisCache().hSetNX(key, field, value);
	}
	public static byte[] hGet2(String key, String field) {
		return factory.getRedisCache().hGet2(key, field);
	}
	public static Map<String, byte[]> hGetAll2(String key) {
		return factory.getRedisCache().hGetAll2(key);
	}
	public static List<byte[]> hMGet2(String key, String... fields) {
		return factory.getRedisCache().hMGet2(key, fields);
	}
	public static void hMSet2(String key, Map<String, byte[]> tuple) {
		factory.getRedisCache().hMSet2(key, tuple);
	}
	public static List<byte[]> hVals2(String key) {
		return factory.getRedisCache().hVals2(key);
	}
	public static <T> T invoke(Callback<T> call) {
		return factory.getRedisCache().invoke(call);
	}
	public static long ttl(Object key) {
		return factory.getRedisCache().ttl(key);
	}
}