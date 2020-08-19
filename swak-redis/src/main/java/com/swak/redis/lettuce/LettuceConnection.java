package com.swak.redis.lettuce;

import java.io.IOException;

import com.swak.redis.RedisAsyncCommands;
import com.swak.redis.RedisAsyncPubSubCommands;
import com.swak.redis.RedisCommands;
import com.swak.redis.RedisConnection;

import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.async.RedisClusterPubSubAsyncCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;

/**
 * 连接
 * 
 * @author lifeng
 * @date 2020年8月19日 下午4:19:05
 */
public class LettuceConnection implements RedisConnection<byte[], byte[]> {
	StatefulConnection<byte[], byte[]> conn;

	public LettuceConnection(StatefulConnection<byte[], byte[]> conn) {
		this.conn = conn;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public RedisCommands<byte[], byte[]> redisCommands() {
		if (conn instanceof StatefulRedisClusterConnection) {
			RedisClusterCommands<byte[], byte[]> comms = ((StatefulRedisClusterConnection<byte[], byte[]>) conn).sync();
			return new LettuceRedisCommands(comms);
		}
		io.lettuce.core.api.sync.RedisCommands<byte[], byte[]> comms = ((StatefulRedisConnection<byte[], byte[]>) conn)
				.sync();
		return new LettuceRedisCommands(comms);
	}

	@Override
	public RedisAsyncCommands<byte[], byte[]> redisAsyncCommands() {
		if (conn instanceof StatefulRedisClusterConnection) {
			RedisAdvancedClusterAsyncCommands<byte[], byte[]> comms = ((StatefulRedisClusterConnection<byte[], byte[]>) conn)
					.async();
			return new LettuceRedisAsyncCommands(comms);
		}
		io.lettuce.core.api.async.RedisAsyncCommands<byte[], byte[]> comms = ((StatefulRedisConnection<byte[], byte[]>) conn)
				.async();
		return new LettuceRedisAsyncCommands(comms);
	}

	@Override
	public RedisAsyncPubSubCommands<byte[], byte[]> redisAsyncPubSubCommands() {
		if (conn instanceof StatefulRedisClusterPubSubConnection) {
			RedisClusterPubSubAsyncCommands<byte[], byte[]> comms = ((StatefulRedisClusterPubSubConnection<byte[], byte[]>) conn)
					.async();
			return new LettuceRedisAsyncPubSubCommands(comms);
		}
		RedisPubSubAsyncCommands<byte[], byte[]> comms = ((StatefulRedisPubSubConnection<byte[], byte[]>) conn).async();
		return new LettuceRedisAsyncPubSubCommands(comms);
	}
}
