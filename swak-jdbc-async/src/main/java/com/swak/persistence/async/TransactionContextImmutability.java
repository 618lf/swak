package com.swak.persistence.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;

/**
 * 事务变量
 * 
 * @author lifeng
 * @date 2020年10月5日 下午10:10:28
 */
class TransactionContextImmutability {

	/**
	 * 连接池
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
	 * 只读 -- 只能用于查询
	 */
	boolean readOnly = false;

	/**
	 * 是否准备OK
	 */
	CompletableFuture<Void> prepared = new CompletableFuture<>();

	/**
	 * 是否提交
	 */
	AtomicBoolean commited = new AtomicBoolean(false);

	/**
	 * 开启
	 */
	void begin() {
		try {
			if (!this.readOnly) {
				this.tx = this.connection.begin();
			}
			this.prepared.complete(null);
		} catch (Exception e) {
			this.prepared.completeExceptionally(e);
		}
	}

	/**
	 * 客户端
	 * 
	 * @return
	 */
	SqlClient channel() {
		return this.tx != null ? this.tx : this.connection;
	}

	/**
	 * 如果只读能需要手动关闭连接
	 * 
	 * @return
	 */
	CompletableFuture<TransactionContext> future() {
		if (!this.readOnly) {
			return new CompletableFuture<TransactionContext>();
		}
		return new CompletableFuture<TransactionContext>().exceptionally(e -> {
			this.close();
			return TransactionContext.error(e);
		});
	}

	/**
	 * 手动关闭连接 -- 或提交事务
	 */
	void close() {
		try {
			this.connection.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 提交
	 * 
	 * @return
	 */
	CompletableFuture<Void> commit() {
		CompletableFuture<Void> completed = new CompletableFuture<>();
		if (!this.readOnly) {
			this.tx.commit(res -> {
				if (res.cause() != null) {
					completed.completeExceptionally(res.cause());
				} else {
					completed.complete(null);
				}
			});
		} else {
			this.close();
			completed.complete(null);
		}
		return completed;
	}
}
