package com.tmt.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;

public class TestMain {

	public static void main(String[] args) {
		RedisURI config = RedisURI.Builder.redis("localhost", 6379).withPassword("12345678....").build();
		RedisClient client = RedisClient.create(config);
		StatefulRedisConnection<String, String> connection = client.connect();
		RedisStringCommands<String, String> sync = connection.sync();
	}
}
