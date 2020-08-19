package com.swak.redis;

import java.io.Closeable;

/**
 * Redis 连接
 * 
 * @author lifeng
 * @date 2020年8月19日 下午3:52:56
 */
public interface RedisConnection<K, V> extends Closeable {

	/**
	 * 同步操作命令集合
	 * 
	 * @return
	 */
	RedisCommands<K, V> redisCommands();

	/**
	 * 异步操作命令集合
	 * 
	 * @return
	 */
	RedisAsyncCommands<K, V> redisAsyncCommands();

	/**
	 * 异步发布订阅命令集合
	 * 
	 * @return
	 */
	RedisAsyncPubSubCommands<K, V> redisAsyncPubSubCommands();
}