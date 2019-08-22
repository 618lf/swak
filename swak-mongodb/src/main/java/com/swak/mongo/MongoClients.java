package com.swak.mongo;

import java.util.concurrent.CompletableFuture;

import org.springframework.util.Assert;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.swak.mongo.json.Document;
import com.swak.utils.Lists;

/**
 * 客户端
 * 
 * @author lifeng
 */
@SuppressWarnings("deprecation")
public class MongoClients {

	private static MongoHolder holder = null;

	public static void setMongoDB(MongoHolder holder) {
		MongoClients.holder = holder;
	}

	/**
	 * 插入数据
	 * 
	 * @param table
	 * @param docs
	 * @return
	 */
	public static CompletableFuture<Void> insert(String table, Document... docs) {
		Assert.notNull(table, "table can not null");
		Assert.notNull(docs, "docs can not null");
		Assert.notEmpty(docs, "docs can not empty");
		CompletableFuture<Void> future = new CompletableFuture<>();
		MongoCollection<Document> collection = holder.db.getCollection(table, Document.class);
		if (docs.length == 1) {
			collection.insertOne(docs[0], (v, r) -> {
				if (r != null) {
					future.completeExceptionally(r);
				} else {
					future.complete(v);
				}
			});
		} else {
			collection.insertMany(Lists.newArrayList(docs), (v, r) -> {
				if (r != null) {
					future.completeExceptionally(r);
				} else {
					future.complete(v);
				}
			});
		}
		return future;
	}

	/**
	 * Mongo Holder
	 * 
	 * @author lifeng
	 */
	public static class MongoHolder {

		@SuppressWarnings("unused")
		private MongoClient mongo;
		private MongoDatabase db;

		public MongoHolder(MongoClient mongo, MongoDatabase db) {
			this.mongo = mongo;
			this.db = db;
		}
	}
}