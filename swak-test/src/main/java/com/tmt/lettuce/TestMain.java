package com.tmt.lettuce;

import java.util.concurrent.CountDownLatch;

import com.swak.common.cache.SafeEncoder;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RedisURI config = RedisURI.Builder.redis("localhost", 6379).withPassword("12345678....").build();
		RedisClient client = RedisClient.create(config);
		StatefulRedisConnection<byte[], byte[]> connection = client.connect(new ByteArrayCodec());
		String key = "test_script";
		connection.sync().set(SafeEncoder.encode(key), SafeEncoder.encode("123"));
		connection.reactive().get(SafeEncoder.encode(key)).subscribe((bs)->{
			System.out.println(SafeEncoder.encode(bs));
		});
		latch.await();
	}
}