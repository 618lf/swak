package com.swak.mongo;

import java.util.concurrent.CountDownLatch;

import com.mongodb.MongoClientSettings;
import com.swak.config.mongo.MongoAutoConfiguration;
import com.swak.config.mongo.MongoProperties;
import com.swak.entity.Parameters;
import com.swak.mongo.json.Document;
import com.swak.utils.JsonMapper;

/**
 * mongo 测试
 * 
 * @author lifeng
 */
public class MongoDbTest {

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		MongoAutoConfiguration config = new MongoAutoConfiguration();
		MongoClientSettings settings = config.settings();
		MongoProperties properties = new MongoProperties();
		properties.setUsername("admin");
		properties.setPassword("123456".toCharArray());
		properties.setDatabase("cloud");
		properties.setAuthenticationDatabase("admin");
		config.mongoClient(settings, properties);
		Goods goods = new Goods();
		Document query = new Document(goods);
		Parameters param = new Parameters();
		MongoClients.page("GOODS", query, param).whenComplete((res, t) -> {
			System.out.println(JsonMapper.toJson(res));
			countDownLatch.countDown();
		});
		countDownLatch.await();
	}
}
