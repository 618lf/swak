package com.swak.mongo.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.mongo.MongoClients;
import com.swak.mongo.json.Document;
import com.swak.mongo.json.Query;
import com.swak.mongo.json.Update;
import com.swak.utils.JsonMapper;
import com.swak.utils.Lists;

/**
 * 基础的 DAO
 * 
 * @author lifeng
 */
public abstract class BaseService<T> {

	/**
	 * 目标类字节码
	 */
	private Class<T> clazz;

	/**
	 * 数据库名称
	 */
	protected abstract String table();

	/**
	 * 获取数据
	 * 
	 * @param table
	 * @param id
	 * @return
	 */
	public CompletableFuture<T> get(String id) {
		return MongoClients.get(table(), id).thenApply(res -> {
			if (res != null) {
				res.put(Document._ID_FIELD, res.get(Document.ID_FIELD));
				return this.toBean(res);
			}
			return null;
		});
	}

	/**
	 * 获取数据
	 * 
	 * @param table
	 * @param id
	 * @return
	 */
	public CompletableFuture<T> get(Query query) {
		return MongoClients.get(table(), query).thenApply(res -> {
			if (res != null) {
				res.put(Document._ID_FIELD, res.get(Document.ID_FIELD));
				return this.toBean(res);
			}
			return null;
		});
	}

	/**
	 * 插入数据
	 * 
	 * @param table
	 * @param entitys
	 * @return
	 */
	public CompletableFuture<T> insert(T entity) {
		Document doc = new Document(entity);
		return MongoClients.insert(table(), doc).thenApply(res -> entity);
	}

	/**
	 * 批量插入数据
	 * 
	 * @param table
	 * @param entitys
	 * @return
	 */
	public CompletableFuture<Void> batchInsert(String table, List<T> entitys) {
		Document[] docs = new Document[entitys.size()];
		for (int i = 0; i < entitys.size(); i++) {
			docs[i] = new Document(entitys.get(i));
		}
		return MongoClients.insert(table(), docs);
	}

	/**
	 * 保存数据
	 * 
	 * @param table
	 * @param entity
	 * @return
	 */
	public CompletableFuture<T> save(T entity) {
		Document doc = new Document(entity);
		return MongoClients.save(table(), doc).thenApply(res -> {
			res.put(Document._ID_FIELD, res.get(Document.ID_FIELD));
			return this.toBean(res);
		});
	}

	/**
	 * 保存数据
	 * 
	 * @param table
	 * @param entity
	 * @return
	 */
	public CompletableFuture<Void> update(T entity, Update update) {
		Document doc = new Document(entity);
		return MongoClients.update(table(), doc, update).thenApply(res -> {
			return null;
		});
	}

	/**
	 * 删除数据
	 * 
	 * @param table
	 * @param entity
	 * @return
	 */
	public CompletableFuture<Long> delete(T entity) {
		Document doc = new Document(entity);
		return MongoClients.delete(table(), doc);
	}

	/**
	 * 查询个数
	 * 
	 * @param query
	 * @return
	 */
	public CompletableFuture<Integer> count(Query query) {
		return MongoClients.count(table(), query);
	}

	/**
	 * 查询
	 * 
	 * @param table
	 * @param entity
	 * @param param
	 * @return
	 */
	public CompletableFuture<Page> page(Query query, Parameters param) {
		return MongoClients.page(table(), query, param).thenApply(page -> {
			List<Document> docs = page.getData();
			List<T> ts = Lists.newArrayList(docs.size());
			for (Document doc : docs) {
				doc.put(Document._ID_FIELD, doc.get(Document.ID_FIELD));
				ts.add(this.toBean(doc));
			}
			page.setData(ts);
			return page;
		});
	}

	/**
	 * 查询
	 * 
	 * @param table
	 * @param entity
	 * @param param
	 * @return
	 */
	public CompletableFuture<Page> page(T entity, Parameters param) {
		Query query = new Query(entity);
		return MongoClients.page(table(), query, param).thenApply(page -> {
			List<Document> docs = page.getData();
			List<T> ts = Lists.newArrayList(docs.size());
			for (Document doc : docs) {
				doc.put(Document._ID_FIELD, doc.get(Document.ID_FIELD));
				ts.add(this.toBean(doc));
			}
			page.setData(ts);
			return page;
		});
	}

	/**
	 * 查询
	 * 
	 * @param table
	 * @param entity
	 * @param param
	 * @return
	 */
	public CompletableFuture<List<T>> query(T entity, int limit) {
		Query query = new Query(entity);
		return MongoClients.query(table(), query, limit).thenApply(docs -> {
			List<T> ts = Lists.newArrayList(docs.size());
			for (Document doc : docs) {
				doc.put(Document._ID_FIELD, doc.get(Document.ID_FIELD));
				ts.add(this.toBean(doc));
			}
			return ts;
		});
	}

	/**
	 * 查询
	 * 
	 * @param table
	 * @param entity
	 * @param param
	 * @return
	 */
	public CompletableFuture<List<T>> query(Query query, int limit) {
		return MongoClients.query(table(), query, limit).thenApply(docs -> {
			List<T> ts = Lists.newArrayList(docs.size());
			for (Document doc : docs) {
				doc.put(Document._ID_FIELD, doc.get(Document.ID_FIELD));
				ts.add(this.toBean(doc));
			}
			return ts;
		});
	}

	/**
	 * bean 的转换
	 * 
	 * @param doc
	 * @return
	 */
	protected T toBean(Document doc) {
		String json = JsonMapper.toJson(doc);
		return JsonMapper.fromJson(json, getTargetClass());
	}

	/**
	 * 目标对象 T 的实际类型
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> getTargetClass() {
		if (clazz == null) {
			clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return clazz;
	}
}