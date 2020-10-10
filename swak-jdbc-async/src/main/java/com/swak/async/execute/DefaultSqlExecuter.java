package com.swak.async.execute;

import com.swak.async.persistence.Sql;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * 默认的Sql执行器
 * 
 * @author lifeng
 * @date 2020年10月10日 上午10:40:38
 */
public class DefaultSqlExecuter implements SqlExecuter {

	/**
	 * 执行Sql， 并异步处理执行结果
	 */
	@Override
	public <T> void execute(Sql<T> sql, Handler<AsyncResult<T>> handler) {

	}
}
