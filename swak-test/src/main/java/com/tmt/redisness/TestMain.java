package com.tmt.redisness;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

/**
 * redisness 测试
 * @author lifeng
 */
public class TestMain {

	public static void main(String[] args) {
		Config config = new Config();
		config.setTransportMode(TransportMode.EPOLL);
		config.useClusterServers().addNodeAddress("redis://127.0.0.1:6379");
		RedissonClient redisson = Redisson.create(config);
	}
}