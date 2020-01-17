package com.swak.mongo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.bson.codecs.BsonTypeClassMap;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.swak.config.mongo.MongoAutoConfiguration;
import com.swak.config.mongo.MongoProperties;
import com.swak.entity.Parameters;
import com.swak.mongo.json.Document;
import com.swak.mongo.json.Query;
import com.swak.utils.JsonMapper;
import com.swak.utils.Lists;
import com.swak.utils.Maps;

/**
 * mongo 测试
 * 
 * @author lifeng
 */
public class MongoDbTest {

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(2);
		MongoAutoConfiguration config = new MongoAutoConfiguration();
		MongoClientSettings settings = config.settings(new BsonTypeClassMap());
		MongoProperties properties = new MongoProperties();
		properties.setUsername("admin");
		properties.setPassword("123456".toCharArray());
		properties.setDatabase("cloud");
		properties.setAuthenticationDatabase("admin");
		config.mongoClient(settings, properties);
		// MongoClients.transaction(() -> {
		// Goods goods = new Goods();
		// goods.setName("李锋");
		// goods.setDay(DateUtils.getTodayDate());
		// Document saved = new Document(goods);
		// return MongoClients.save("TEST_GOODS", saved);
		// }).whenComplete((res, t) -> {
		// if (t != null) {
		// t.printStackTrace();
		// }
		// System.out.println(JsonMapper.toJson(res));
		// countDownLatch.countDown();
		// });
		// Document query = new Document();
		// Parameters param = new Parameters();
		// MongoClients.page("TEST_GOODS", query, param).whenComplete((res, t) -> {
		// System.out.println(JsonMapper.toJson(res));
		// countDownLatch.countDown();
		// });

		// Resource resource = new Resource();
		// resource.setId("5d71c68ce7a76170928fcdeb");
		// resource.setState(1);
		// Document query = new Document(resource);
		// MongoClients.save("RESOURCE", query).whenComplete((res, t) -> {
		// System.out.println(JsonMapper.toJson(res));
		// countDownLatch.countDown();
		// });

		Resource insert = new Resource();
		insert.setState(1);
		insert.setStoreName("test");
		insert.setCreateDate(LocalDateTime.now());
		insert.setBoo(true);
		insert.setDou(1.0);
		insert.setLon(1L);
		insert.setBigd(BigDecimal.valueOf(1.0));
		insert.setByt(1);
		MongoClients.save("RESOURCE", new Document(insert)).whenComplete((res, t) -> {
			System.out.println(JsonMapper.toJson(res));
			countDownLatch.countDown();
		});

		Resource resource = new Resource();
		resource.setState(1);
		Query query = new Query(resource);
		query.and(Filters.eq("storeName", "test"));
		query.fields(Projections.include("boo", "dou", "lon", "bigd", "byt"));
		Parameters param = new Parameters();
		MongoClients.page("RESOURCE", query, param).whenComplete((page, t) -> {
			List<Document> docs = page.getData();
			List<Resource> ts = Lists.newArrayList(docs.size());
			try {
				for (Document doc : docs) {
					Resource bean = new Resource();
					doc.put(Document._ID_FIELD, doc.get(Document.ID_FIELD));
					Maps.toBean(doc, bean);
					ts.add(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			page.setData(ts);
			System.out.println(JsonMapper.toJson(page));
			countDownLatch.countDown();
		});

		// Query query = new Query();
		// query.put("_id", "5d71c5fae7a76170928fcdea");
		// MongoCollection<Document> collection =
		// MongoClients.holder.db.getCollection("RESOURCE", Document.class);
		// FindIterable<Document> find = collection.find(query, Document.class);
		// find.limit(1).filter(Filters.eq("storeName", "lifeng"));
		// List<Document> results = Lists.newArrayList();
		// find.into(results, (v, r) -> {
		// System.out.println(JsonMapper.toJson(v));
		// countDownLatch.countDown();
		// });
		countDownLatch.await();
	}
}
