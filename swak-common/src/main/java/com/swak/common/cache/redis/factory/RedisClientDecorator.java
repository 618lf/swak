package com.swak.common.cache.redis.factory;

import org.springframework.beans.factory.DisposableBean;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * redis client
 * 
 * @author lifeng
 */
public class RedisClientDecorator implements DisposableBean{

	private RedisClient client;
	private RedisCodec<byte[], byte[]> codec = new ByteArrayCodec();

	public RedisClientDecorator(RedisClient client) {
		this.client = client;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends StatefulConnection<byte[], byte[]>> T getConnection(Class<?> connectionType) {
		if (connectionType.isAssignableFrom(StatefulRedisPubSubConnection.class)) {
			return (T) client.connectPubSub(codec);
		}
		
		return (T) client.connect(codec);
	}

	/**
	 * 关闭链接
	 */
	@Override
	public void destroy() throws Exception {
		client.shutdown();
	}
}