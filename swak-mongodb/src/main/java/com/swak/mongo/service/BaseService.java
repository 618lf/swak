package com.swak.mongo.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.mongo.MongoClients;
import com.swak.mongo.json.Document;
import com.swak.utils.Lists;
import com.swak.utils.Maps;

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
				T bean = this.newInstance();
				res.put(Document._ID_FIELD, res.get(Document.ID_FIELD));
				Maps.toBean(res, bean);
				return bean;
			}
			return null;
		});
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
			T bean = this.newInstance();
			res.put(Document._ID_FIELD, res.get(Document.ID_FIELD));
			Maps.toBean(doc, bean);
			return bean;
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
	 * 查询
	 * 
	 * @param table
	 * @param entity
	 * @param param
	 * @return
	 */
	public CompletableFuture<Page> page(Document query, Parameters param) {
		return MongoClients.page(table(), query, param).thenApply(page -> {
			List<Document> docs = page.getData();
			List<T> ts = Lists.newArrayList(docs.size());
			for (Document doc : docs) {
				T bean = this.newInstance();
				doc.put(Document._ID_FIELD, doc.get(Document.ID_FIELD));
				Maps.toBean(doc, bean);
				ts.add(bean);
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
		Document query = new Document(entity);
		return MongoClients.page(table(), query, param).thenApply(page -> {
			List<Document> docs = page.getData();
			List<T> ts = Lists.newArrayList(docs.size());
			for (Document doc : docs) {
				T bean = this.newInstance();
				doc.put(Document._ID_FIELD, doc.get(Document.ID_FIELD));
				Maps.toBean(doc, bean);
				ts.add(bean);
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
		Document query = new Document(entity);
		return MongoClients.query(table(), query, limit).thenApply(docs -> {
			List<T> ts = Lists.newArrayList(docs.size());
			for (Document doc : docs) {
				T bean = this.newInstance();
				doc.put(Document._ID_FIELD, doc.get(Document.ID_FIELD));
				Maps.toBean(doc, bean);
				ts.add(bean);
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
	public CompletableFuture<List<T>> query(Document query, int limit) {
		return MongoClients.query(table(), query, limit).thenApply(docs -> {
			List<T> ts = Lists.newArrayList(docs.size());
			for (Document doc : docs) {
				T bean = this.newInstance();
				doc.put(Document._ID_FIELD, doc.get(Document.ID_FIELD));
				Maps.toBean(doc, bean);
				ts.add(bean);
			}
			return ts;
		});
	}

	/**
	 * 创建实例
	 * 
	 * @return
	 */
	protected T newInstance() {
		try {
			return this.getTargetClass().newInstance();
		} catch (Exception e) {
			return null;
		}
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