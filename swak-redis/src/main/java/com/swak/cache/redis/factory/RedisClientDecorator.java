package com.swak.cache.redis.factory;

import org.springframework.beans.factory.DisposableBean;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;

/**
 * redis client 集群操作
 * 
 * @author lifeng
 */
public class RedisClientDecorator implements DisposableBean, IRedisClient {

	private AbstractRedisClient client;
	private RedisCodec<byte[], byte[]> codec = new ByteArrayCodec();

	public RedisClientDecorator(AbstractRedisClient client) {
		this.client = client;
	}

	/**
	 * 订阅和发布是不同的链接 在一个链接上订阅，另外一个链接上发布
	 * 
	 * @param connectionType
	 * @return
	 */
	public <T extends StatefulConnection<byte[], byte[]>> T getConnection(ConnectType connectionType) {
		if (connectionType == ConnectType.Publish) {
			return this.connectPubSub();
		} else if (connectionType == ConnectType.Subscribe) {
			return this.connectPubSub();
		}
		return this.connect();
	}

	/**
	 * 关闭链接
	 */
	@Override
	public void destroy() throws Exception {
		client.shutdown();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends StatefulConnection<byte[], byte[]>> T connect() {
		if (client instanceof RedisClient) {
			return (T) ((RedisClient) client).connect(codec);
		}
		return (T) ((RedisClusterClient) client).connect(codec);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends StatefulConnection<byte[], byte[]>> T connectPubSub() {
		if (client instanceof RedisClient) {
			return (T) ((RedisClient) client).connectPubSub(codec);
		}
		return (T) ((RedisClusterClient) client).connectPubSub(codec);
	}
}