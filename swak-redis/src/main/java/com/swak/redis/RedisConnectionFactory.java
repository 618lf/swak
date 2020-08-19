package com.swak.redis;

import org.springframework.beans.factory.DisposableBean;

/**
 * 基于 NIO 的异步链接，自动重链
 * 
 * @author lifeng
 */
public interface RedisConnectionFactory<K, V> extends DisposableBean {

	/**
	 * 获取链接
	 * 
	 * @param connectionType 连接类型
	 * @return 获取连接
	 */
	RedisConnection<K, V> getConnection(ConnectType connectionType);
}