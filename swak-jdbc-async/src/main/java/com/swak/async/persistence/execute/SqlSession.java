package com.swak.async.persistence.execute;

import java.util.concurrent.CompletableFuture;

import com.swak.async.persistence.Sql;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.SqlResult;
import com.swak.async.persistence.datasource.DataSource;
import com.swak.async.persistence.sqls.Dml;
import com.swak.persistence.MS;

import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;

/**
 * 一次或一批Sql执行
 * 
 * @author lifeng
 * @date 2020年10月11日 下午10:36:12
 */
public class SqlSession {

	/**
	 * 连接池：可能是一个代理
	 */
	DataSource dataSource;

	/**
	 * 连接
	 */
	SqlConnection connection;

	/**
	 * 事务
	 */
	Transaction tx;

	/**
	 * 是否准备OK
	 */
	CompletableFuture<SqlClient> prepared = new CompletableFuture<>();

	/**
	 * 创建Sql会话
	 * 
	 * @param pool
	 */
	SqlSession(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 是否只读、Sql类型、分库参数、分表参数
	 * 
	 * @param <T>
	 * @param sql
	 * @param handler
	 */
	public <T> CompletableFuture<SqlResult> execute(Sql<T> sql, boolean transaction, SqlParam<T> param) {
		return this.select(transaction, sql).thenCompose(client -> {
			return sql.execute(client, param);
		});
	}

	/**
	 * 是否只读、Sql类型、分库参数、分表参数: 只执行一次
	 * 
	 * @param <T>
	 * @param sql
	 * @param handler
	 */
	public <T> CompletableFuture<SqlResult> execute(Sql<T> sql, SqlParam<T> param) {
		return this.select(false, sql).thenCompose(client -> {
			return sql.execute(client, param);
		});
	}

	/**
	 * 开启
	 */
	<T> CompletableFuture<SqlClient> select(boolean transaction, Sql<T> sql) {
		if (!this.prepared.isDone()) {
			try {
				this.dataSource.getConnection(this.getType(transaction, sql), (res) -> {
					if (res.cause() != null) {
						this.prepared.completeExceptionally(res.cause());
					} else {
						this.connection = res.result();
						this.selectTx(transaction, sql);
					}
				});
			} catch (Exception e) {
				this.prepared.completeExceptionally(e);
			}
		}
		return this.prepared;
	}

	<T> MS getType(boolean transaction, Sql<T> sql) {
		return (sql instanceof Dml || transaction) ? MS.Master : MS.Slave;
	}

	<T> void selectTx(boolean transaction, Sql<T> sql) {
		try {
			if (sql instanceof Dml || transaction) {
				this.connection.begin().onComplete(res -> {
					if (res.succeeded()) {
						this.tx = res.result();
						this.prepared.complete(this.connection);
					} else {
						this.prepared.completeExceptionally(res.cause());
					}
				});
			} else {
				this.prepared.complete(this.connection);
			}
		} catch (Exception e) {
			this.prepared.completeExceptionally(e);
		}
	}

	/**
	 * 结束
	 * 
	 * @return
	 */
	public CompletableFuture<Void> finish(Throwable error) {
		if (error != null && error.getClass().isAssignableFrom(RuntimeException.class)) {
			return this.rollback();
		}
		return this.commit();
	}

	/**
	 * 提交
	 * 
	 * @return
	 */
	public CompletableFuture<Void> commit() {
		CompletableFuture<Void> completed = new CompletableFuture<>();
		if (this.tx != null) {
			this.tx.commit(res -> {
				if (res.cause() != null) {
					completed.completeExceptionally(res.cause());
				} else {
					completed.complete(null);
				}
			});
		} else {
			this.connection.close();
			completed.complete(null);
		}
		return completed;
	}

	/**
	 * 回滚
	 * 
	 * @return
	 */
	public CompletableFuture<Void> rollback() {
		CompletableFuture<Void> completed = new CompletableFuture<>();
		if (this.tx != null) {
			this.tx.rollback(res -> {
				if (res.cause() != null) {
					completed.completeExceptionally(res.cause());
				} else {
					completed.complete(null);
				}
			});
		} else {
			this.connection.close();
			completed.complete(null);
		}
		return completed;
	}
}
