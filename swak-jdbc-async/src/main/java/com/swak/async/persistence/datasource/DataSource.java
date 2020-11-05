package com.swak.async.persistence.datasource;

import com.swak.persistence.MS;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;

/**
 * 数据源
 * 
 * @author lifeng
 * @date 2020年10月13日 下午8:54:23
 */
public class DataSource {

	/**
	 * 连接池
	 */
	Pool pool;

	/**
	 * 创建空连接的数据源
	 */
	public DataSource() {
	}

	/**
	 * 创建数据源
	 * 
	 * @param pool
	 */
	public DataSource(Pool pool) {
		this.pool = pool;
	}

	public void getConnection(MS type, Handler<AsyncResult<SqlConnection>> handler) {
		this.pool.getConnection(handler);
	}
}