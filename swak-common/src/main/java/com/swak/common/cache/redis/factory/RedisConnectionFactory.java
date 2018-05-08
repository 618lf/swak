package com.swak.common.cache.redis.factory;

import org.springframework.beans.factory.DisposableBean;

import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * 获得链接
 * @author lifeng
 */
public interface RedisConnectionFactory<K, V> extends DisposableBean {

	/**
	 * 获取链接
	 * @return
	 */
	StatefulConnection<K, V> getConnection(Class<?> connectionType);
	
	/**
	 * 存储数据
	 * @return
	 */
	default StatefulRedisConnection<K, V> getStandardConnection() {
		return (StatefulRedisConnection<K, V>)this.getConnection(StatefulRedisConnection.class);
	}
	
	/**
	 * 发布订阅
	 * @return
	 */
	default StatefulRedisPubSubConnection<K, V> getPubsubConnection() {
		return (StatefulRedisPubSubConnection<K, V>)this.getConnection(StatefulRedisPubSubConnection.class);
	}
}