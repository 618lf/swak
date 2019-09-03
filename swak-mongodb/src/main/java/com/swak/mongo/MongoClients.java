package com.swak.mongo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.util.Assert;

import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.mongo.json.Document;
import com.swak.mongo.json.DocumentBsonAdapter;
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
	 * 根据ID获取数据
	 * 
	 * @param table
	 * @param id
	 * @return
	 */
	public static CompletableFuture<Document> get(String table, Object id) {
		Assert.notNull(table, "table can not null");
		Assert.notNull(id, "id can not null");
		CompletableFuture<Document> future = new CompletableFuture<>();
		MongoCollection<Document> collection = holder.db.getCollection(table, Document.class);
		Document query = new Document();
		query.put(Document.ID_FIELD, id);
		collection.find(DocumentBsonAdapter.wrap(query)).first((v, r) -> {
			if (r != null) {
				future.completeExceptionally(r);
			} else {
				future.complete(v);
			}
		});
		return future;
	}

	/**
	 * 根据ID获取数据
	 * 
	 * @param table
	 * @param id
	 * @return
	 */
	public static CompletableFuture<Page> query(String table, Document query, Parameters param) {
		Assert.notNull(table, "table can not null");
		Assert.notNull(query, "query can not null");
		CompletableFuture<Page> future = new CompletableFuture<>();
		MongoCollection<Document> collection = holder.db.getCollection(table, Document.class);
		FindIterable<Document> find = collection.find(DocumentBsonAdapter.wrap(query), Document.class);
		find.limit(param.getPageSize()).skip(param.getPageIndex() * param.getPageSize());
		List<Document> results = Lists.newArrayList();
		find.into(results, (v, r) -> {
			if (r != null) {
				future.completeExceptionally(r);
			} else {
				future.complete(new Page(param, results));
			}
		});
		return future;
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
	 * 保存数据
	 * 
	 * @param table
	 * @param doc
	 * @return
	 */
	public static CompletableFuture<Void> save(String table, Document doc) {
		Assert.notNull(table, "table can not null");
		Assert.notNull(doc, "doc can not null");
		CompletableFuture<Void> future = new CompletableFuture<>();
		MongoCollection<Document> collection = holder.db.getCollection(table, Document.class);
		Object id = doc.get(Document.ID_FIELD);
		if (id == null) {
			collection.insertOne(doc, (v, r) -> {
				if (r != null) {
					future.completeExceptionally(r);
				} else {
					future.complete(v);
				}
			});
		} else {
			com.mongodb.client.model.UpdateOptions options = new com.mongodb.client.model.UpdateOptions().upsert(true);
			Document filter = new Document();
			collection.replaceOne(DocumentBsonAdapter.wrap(filter), doc, options, (v, r) -> {
				if (r != null) {
					future.completeExceptionally(r);
				} else {
					future.complete(null);
				}
			});
		}
		return future;
	}

	/**
	 * 保存数据
	 * 
	 * @param table
	 * @param doc
	 * @return
	 */
	public static CompletableFuture<Long> delete(String table, Document doc) {
		Assert.notNull(table, "table can not null");
		Assert.notNull(doc, "doc can not null");
		CompletableFuture<Long> future = new CompletableFuture<>();
		MongoCollection<Document> collection = holder.db.getCollection(table, Document.class);
		Object id = doc.get(Document.ID_FIELD);
		if (id == null) {
			collection.deleteOne(DocumentBsonAdapter.wrap(doc), (v, r) -> {
				if (r != null) {
					future.completeExceptionally(r);
				} else {
					future.complete(v.getDeletedCount());
				}
			});
		} else {
			collection.deleteMany(DocumentBsonAdapter.wrap(doc), (v, r) -> {
				if (r != null) {
					future.completeExceptionally(r);
				} else {
					future.complete(v.getDeletedCount());
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
