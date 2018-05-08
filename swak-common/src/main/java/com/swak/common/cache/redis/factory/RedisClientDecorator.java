package com.swak.common.cache.redis.factory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * redis client
 * 
 * @author lifeng
 */
public class RedisClientDecorator {

	private RedisClient client;
	private RedisCodec<String, byte[]> codec = new StringByteArrayCodec();

	public RedisClientDecorator(RedisClient client) {
		this.client = client;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends StatefulConnection<String, byte[]>> T getConnection(Class<?> connectionType) {
		if (connectionType.isAssignableFrom(StatefulRedisPubSubConnection.class)) {
			return (T) client.connectPubSub(codec);
		}
		
		return (T) client.connect(codec);
	}
}