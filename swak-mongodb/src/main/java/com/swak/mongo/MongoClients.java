package com.swak.mongo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.bson.conversions.Bson;
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
	public static CompletableFuture<Page> page(String table, Document query, Parameters param) {
		Assert.notNull(table, "table can not null");
		Assert.notNull(query, "query can not null");
		CompletableFuture<Page> future = new CompletableFuture<>();
		MongoCollection<Document> collection = holder.db.getCollection(table, Document.class);
		Bson $query = DocumentBsonAdapter.wrap(query);
		collection.countDocuments($query, (v, r) -> {
			if (r != null) {
				future.completeExceptionally(r);
			} else {
				param.setRecordCount(v.intValue());
				_query(collection, $query, param, future);
			}
		});
		return future;
	}

	private static void _query(MongoCollection<Document> collection, Bson $query, Parameters param,
			CompletableFuture<Page> future) {
		FindIterable<Document> find = collection.find($query, Document.class);
		find.limit(param.getPageSize()).skip((param.getPageIndex() - 1) * param.getPageSize());
		List<Document> results = Lists.newArrayList();
		find.into(results, (v, r) -> {
			if (r != null) {
				future.completeExceptionally(r);
			} else {
				future.complete(new Page(param, results));
			}
		});
	}

	/**
	 * 根据ID获取数据
	 * 
	 * @param table
	 * @param id
	 * @return
	 */
	public static CompletableFuture<List<Document>> query(String table, Document query, int limit) {
		Assert.notNull(table, "table can not null");
		Assert.notNull(query, "query can not null");
		CompletableFuture<List<Document>> future = new CompletableFuture<>();
		MongoCollection<Document> collection = holder.db.getCollection(table, Document.class);
		FindIterable<Document> find = collection.find(DocumentBsonAdapter.wrap(query), Document.class);
		find.limit(limit);
		List<Document> results = Lists.newArrayList();
		find.into(results, (v, r) -> {
			if (r != null) {
				future.completeExceptionally(r);
			} else {
				future.complete(results);
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
	public static CompletableFuture<Document> save(String table, Document doc) {
		Assert.notNull(table, "table can not null");
		Assert.notNull(doc, "doc can not null");
		CompletableFuture<Document> future = new CompletableFuture<>();
		MongoCollection<Document> collection = holder.db.getCollection(table, Document.class);
		Object id = doc.get(Document.ID_FIELD);
		if (id == null) {
			collection.insertOne(doc, (v, r) -> {
				if (r != null) {
					future.completeExceptionally(r);
				} else {
					future.complete(doc);
				}
			});
		} else {
			com.mongodb.client.model.UpdateOptions options = new com.mongodb.client.model.UpdateOptions().upsert(true);
			Document filter = new Document();
			filter.put(Document.ID_FIELD, id);
			collection.replaceOne(DocumentBsonAdapter.wrap(filter), doc, options, (v, r) -> {
				if (r != null) {
					future.completeExceptionally(r);
				} else {
					future.complete(doc);
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
	 * Mongdb 事务（单机版本不支持事务）
	 * 
	 * @param future
	 * @return
	 */
	public static <T> CompletableFuture<T> transaction(Supplier<CompletableFuture<T>> supplier) {
		Assert.notNull(supplier, "table can not null");
		CompletableFuture<T> future = new CompletableFuture<>();
		holder.mongo.startSession((session, t) -> {
			if (t != null) {
				future.completeExceptionally(t);
			} else {
				try {
					session.startTransaction();
					supplier.get().whenComplete((r1, t1) -> {
						if (t1 != null) {
							session.abortTransaction((a1, a2) -> {
								future.completeExceptionally(t1);
							});
						} else {
							session.commitTransaction((c1, c2) -> {
								future.complete(r1);
							});
						}
					});
				} catch (Exception e) {
					future.completeExceptionally(e);
				}
			}
		});
		return future;
	}

	/**
	 * Mongo Holder
	 * 
	 * @author lifeng
	 */
	public static class MongoHolder {

		private MongoClient mongo;
		private MongoDatabase db;

		public MongoHolder(MongoClient mongo, MongoDatabase db) {
			this.mongo = mongo;
			this.db = db;
		}
	}
}
