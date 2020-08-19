package com.swak.redis.lettuce;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.redis.ConnectType;
import com.swak.redis.RedisConnection;
import com.swak.redis.RedisConnectionFactory;

/**
 * 连接管理器
 * 
 * @author lifeng
 * @date 2020年8月19日 下午4:10:24
 */
public class LettuceConnectionFactory implements RedisConnectionFactory<byte[], byte[]> {

	private final LettuceClientDecorator client;
	private final Map<ConnectType, RedisConnection<byte[], byte[]>> pools;

	public LettuceConnectionFactory(LettuceClientDecorator client) {
		this.client = client;
		pools = new ConcurrentHashMap<>(3);
	}

	@Override
	public RedisConnection<byte[], byte[]> getConnection(ConnectType connectionType) {
		try {
			return pools.computeIfAbsent(connectionType, (poolType) -> {
				return client.getConnection(connectionType);
			});
		} catch (Exception e) {
			throw new RuntimeException("连接Redis服务器失败,请检查服务器地址、端口、密码、防火墙是否配置正确：", e);
		}
	}

	@Override
	public void destroy() throws Exception {
		pools.values().stream().forEach(connect -> {
			try {
				connect.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		pools.clear();
	}
}
