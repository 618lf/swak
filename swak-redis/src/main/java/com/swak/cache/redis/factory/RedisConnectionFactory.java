package com.swak.cache.redis.factory;

import org.springframework.beans.factory.DisposableBean;

import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * 基于 NIO 的异步链接，自动重链
 * 
 * @author lifeng
 */
public interface RedisConnectionFactory<K, V> extends DisposableBean {

	/**
	 * 获取链接
	 * @return
	 */
	StatefulConnection<K, V> getConnection(ConnectType connectionType);
	
	/**
	 * 存储数据
	 * @return
	 */
	default StatefulRedisConnection<K, V> getStandardConnection() {
		return (StatefulRedisConnection<K, V>)this.getConnection(ConnectType.Standard);
	}
	
	// -------- 发布订阅不能是同一个链接,否则 Redis 会抛出异常 -------------------
	/**
	 * 发布 - 链接
	 * @return
	 */
	default StatefulRedisPubSubConnection<K, V> getPublishConnection() {
		return (StatefulRedisPubSubConnection<K, V>)this.getConnection(ConnectType.Publish);
	}
	
	/**
	 * 订阅 - 链接
	 * @return
	 */
	default StatefulRedisPubSubConnection<K, V> getSubscribeConnection() {
		return (StatefulRedisPubSubConnection<K, V>)this.getConnection(ConnectType.Subscribe);
	}
}