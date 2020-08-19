package com.swak.redis.lettuce;

import org.springframework.beans.factory.DisposableBean;

import com.swak.redis.ConnectType;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;

/**
 * redis client 集群操作
 * 
 * @author lifeng
 */
public class LettuceClientDecorator implements DisposableBean {

	private AbstractRedisClient client;
	private RedisCodec<byte[], byte[]> codec = new ByteArrayCodec();

	public LettuceClientDecorator(AbstractRedisClient client) {
		this.client = client;
	}

	/**
	 * 订阅和发布是不同的链接 在一个链接上订阅，另外一个链接上发布
	 * 
	 * @param connectionType
	 * @return
	 */
	public <T extends LettuceConnection> T getConnection(ConnectType connectionType) {
		if (connectionType == ConnectType.PubSub) {
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

	@SuppressWarnings("unchecked")
	public <T extends LettuceConnection> T connect() {
		if (client instanceof RedisClient) {
			return (T) new LettuceConnection(((RedisClient) client).connect(codec));
		}
		return (T) new LettuceConnection(((RedisClusterClient) client).connect(codec));
	}

	@SuppressWarnings("unchecked")
	public <T extends LettuceConnection> T connectPubSub() {
		if (client instanceof RedisClient) {
			return (T) new LettuceConnection(((RedisClient) client).connectPubSub(codec));
		}
		return (T) new LettuceConnection(((RedisClusterClient) client).connectPubSub(codec));
	}
}