package com.swak.async.persistence;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.persistence.QueryCondition;

import io.vertx.sqlclient.SqlClient;

/**
 * SQL 语句
 * 
 * @author lifeng
 * @date 2020年10月7日 下午11:30:33
 */
public interface Sql<T> {

	/**
	 * 执行Sql
	 * 
	 * @param <U>
	 * @param client
	 * @param transaction
	 * @param entity
	 * @param query
	 * @return
	 */
	<U> CompletableFuture<List<U>> execute(SqlClient client, T entity, QueryCondition query);
}
