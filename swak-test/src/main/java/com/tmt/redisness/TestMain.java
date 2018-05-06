package com.tmt.redisness;

import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

/**
 * redisness 测试
 * @author lifeng
 */
public class TestMain {

	public static void main(String[] args) {
		Config config = new Config();
		config.setTransportMode(TransportMode.NIO);
		config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("12345678....");
		RedissonReactiveClient client = Redisson.createReactive(config);
		client.getList("ss").add("12");
	}
}