package com.swak.common.cache.redis;

import com.swak.common.cache.redis.factory.IRedisCacheUtils;
import com.swak.common.cache.redis.factory.RedisConnectionFactory;
import com.swak.common.utils.SpringContextHolder;


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
	 * 获得 redis 操作对象
	 * @return
	 */
    public static IRedisCacheUtils getRedis() {
    	return factory.getRedisCache();
    }
}