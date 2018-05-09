package com.swak.common.cache.redis.factory;

import org.springframework.beans.factory.DisposableBean;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;

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
	
	/**
	 * 订阅和发布是不同的链接
	 * 在一个链接上订阅，另外一个链接上发布
	 * @param connectionType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends StatefulConnection<byte[], byte[]>> T getConnection(ConnectType connectionType) {
		if (connectionType == ConnectType.Publish) {
			return (T) client.connectPubSub(codec);
		} else if (connectionType == ConnectType.Subscribe) {
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