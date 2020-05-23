package com.swak.cache.redis.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.lettuce.core.api.StatefulConnection;

/**
 * 池化处理
 * 
 * @author lifeng
 */
public class RedisConnectionPoolFactory implements RedisConnectionFactory<byte[], byte[]> {

	private final RedisClientDecorator client;
	private final Map<ConnectType, StatefulConnection<byte[], byte[]>> pools;

	public RedisConnectionPoolFactory(RedisClientDecorator client) {
		this.client = client;
		pools = new ConcurrentHashMap<>(3);
	}

	/**
	 * 获取链接
	 */
	@Override
	public StatefulConnection<byte[], byte[]> getConnection(ConnectType connectionType) {
		try {
			return pools.computeIfAbsent(connectionType, (poolType) -> {
				return client.getConnection(connectionType);
			});
		} catch (Exception e) {
			throw new RuntimeException("连接Redis服务器失败,请检查服务器地址、端口、密码、防火墙是否配置正确：", e);
		}
	}

	/**
	 * 关闭链接
	 */
	@Override
	public void destroy() throws Exception {
		pools.values().stream().forEach(connect -> connect.close());
		pools.clear();
	}
}