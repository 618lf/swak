package com.tmt.lettuce;

import com.swak.common.cache.SafeEncoder;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;

public class TestMain {

	public static void main(String[] args) {
		RedisURI config = RedisURI.Builder.redis("localhost", 6379).withPassword("12345678....").build();
		RedisClient client = RedisClient.create(config);
		StatefulRedisConnection<byte[], byte[]> connection = client.connect(new ByteArrayCodec());

		String key = "test_script";
		int timeToIdle = 1000;
		byte[][] values = new byte[][] { SafeEncoder.encode(key), SafeEncoder.encode(String.valueOf(timeToIdle)) };
		byte[][] _values = new byte[][] {};
		connection.sync().set(SafeEncoder.encode(key), SafeEncoder.encode("123"));
		connection.sync().eval("redis.call(\"EXPIRE\", KEYS[1], KEYS[2])", ScriptOutputType.VALUE,
				values, _values);
	}
}