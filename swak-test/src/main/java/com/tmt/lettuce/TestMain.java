package com.tmt.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;

public class TestMain {

	public static void main(String[] args) {
		RedisClient client = RedisClient.create("redis://127.0.0.1:6379");
		StatefulRedisConnection<String, String> connection = client.connect();
		RedisStringCommands<String, String> sync = connection.sync();
	}
}
