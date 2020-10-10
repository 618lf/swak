package com.swak.async.execute;

import com.swak.async.persistence.Sql;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Sql 执行器: 管理整个Sql 的执行，实现Sql的二次处理： 读写分离，分表
 * 
 * @author lifeng
 * @date 2020年10月10日 上午9:42:26
 */
public interface SqlExecuter {

	/**
	 * 执行 Sql, 通过回调来异步处理
	 */
	<T> void execute(Sql<T> sql, Handler<AsyncResult<T>> handler);
}