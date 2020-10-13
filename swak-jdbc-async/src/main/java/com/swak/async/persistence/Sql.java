package com.swak.async.persistence;

import java.util.concurrent.CompletableFuture;

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
	 * @param client 执行器
	 * @param param  参数
	 * @return
	 */
	CompletableFuture<SqlResult> execute(SqlClient client, SqlParam<T> param);

	/**
	 * 参数
	 * 
	 * @return
	 */
	SqlParam<T> newParam();
}
