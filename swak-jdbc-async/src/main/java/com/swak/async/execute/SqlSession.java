package com.swak.async.execute;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.async.persistence.Sql;
import com.swak.async.persistence.sqls.Dml;
import com.swak.persistence.QueryCondition;

import io.vertx.sqlclient.Pool;
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
	Pool pool;

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
	SqlSession(Pool pool) {
		this.pool = pool;
	}

	/**
	 * 是否只读、Sql类型、分库参数、分表参数
	 * 
	 * @param <T>
	 * @param sql
	 * @param handler
	 */
	public <T> CompletableFuture<List<T>> execute(Sql<T> sql, boolean transaction, T entity, QueryCondition query) {
		return this.select(transaction, sql).thenCompose(client -> {
			return sql.execute(client, entity, query);
		});
	}

	/**
	 * 是否只读、Sql类型、分库参数、分表参数: 只执行一次
	 * 
	 * @param <T>
	 * @param sql
	 * @param handler
	 */
	public <T> CompletableFuture<List<T>> execute(Sql<T> sql, T entity, QueryCondition query) {
		return this.selectPool(false, sql).thenCompose(client -> {
			return sql.execute(client, entity, query);
		});
	}

	/**
	 * 开启
	 */
	<T> CompletableFuture<SqlClient> select(boolean transaction, Sql<T> sql) {
		if (!this.prepared.isDone()) {
			try {
				this.pool.getConnection((res) -> {
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

	/**
	 * 开启
	 */
	<T> CompletableFuture<SqlClient> selectPool(boolean transaction, Sql<T> sql) {
		if (!this.prepared.isDone()) {
			this.prepared.complete(this.pool);
		}
		return this.prepared;
	}

	/**
	 * 开启事务
	 * 
	 * @param sql
	 */
	<T> void selectTx(boolean transaction, Sql<T> sql) {
		try {
			if (sql instanceof Dml || transaction) {
				this.tx = this.connection.begin();
				this.prepared.complete(this.tx);
			} else {
				this.prepared.complete(this.connection);
			}
		} catch (Exception e) {
			this.prepared.completeExceptionally(e);
		}
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
