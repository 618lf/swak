package com.swak.mongo.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.mongo.MongoClients;
import com.swak.mongo.json.Document;

/**
 * 基础的 DAO
 * 
 * @author lifeng
 */
public class BaseService<T> {

	/**
	 * 插入数据
	 * 
	 * @param table
	 * @param entity
	 * @return
	 */
	public CompletableFuture<Void> insert(String table, T entity) {
		Document doc = new Document(entity);
		return MongoClients.insert(table, doc);
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
		return MongoClients.insert(table, docs);
	}

	/**
	 * 保存数据
	 * 
	 * @param table
	 * @param entity
	 * @return
	 */
	public CompletableFuture<Void> save(String table, T entity) {
		Document doc = new Document(entity);
		return MongoClients.save(table, doc);
	}

	/**
	 * 删除数据
	 * 
	 * @param table
	 * @param entity
	 * @return
	 */
	public CompletableFuture<Void> delete(String table, T entity) {
		Document doc = new Document(entity);
		return MongoClients.delete(table, doc);
	}

	/**
	 * 查询
	 * 
	 * @param table
	 * @param entity
	 * @param param
	 * @return
	 */
	public CompletableFuture<Page> query(String table, T entity, Parameters param) {
		Document query = new Document(entity);
		return MongoClients.query(table, query, param);
	}
}