package com.swak.async.execute;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.async.datasource.DataSource;
import com.swak.async.persistence.Sql;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.SqlResult;
import com.swak.async.tx.TransactionContext;
import com.swak.persistence.QueryCondition;

/**
 * Sql 执行器: 管理整个Sql 的执行，实现Sql的二次处理： 读写分离，分表
 * 
 * @author lifeng
 * @date 2020年10月10日 上午9:42:26
 */
public class SqlExecuter {

	/**
	 * 连接池 -- 可能有多个, 或者是一个代理
	 */
	DataSource dataSource;

	public SqlExecuter(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 只读事务 -- 其实是为了多次查询使用一次连接
	 * 
	 * @param context
	 * @return
	 */
	public <T> TransactionContext<T> beginQuery(TransactionContext<T> context) {
		TransactionContext<T> continueTransactionContext = context;
		if (continueTransactionContext == null || continueTransactionContext.isCommited()) {
			continueTransactionContext = TransactionContext.of(open(), true);
		}
		return continueTransactionContext;
	}

	/**
	 * 开启事务
	 * 
	 * @param context
	 * @return
	 */
	public <T> TransactionContext<T> beginTransaction(TransactionContext<T> context) {
		TransactionContext<T> continueTransactionContext = context;
		if (continueTransactionContext == null || continueTransactionContext.isCommited()) {
			continueTransactionContext = TransactionContext.of(open(), false);
		}
		return continueTransactionContext;
	}

	/**
	 * 打开一个未准备好的 Session
	 * 
	 * @return
	 */
	public SqlSession open() {
		return new SqlSession(dataSource);
	}

	/**
	 * 执行Sql
	 */
	private <T> SessionFuture<SqlResult> execute(Sql<T> sql, SqlParam<T> param) {
		SqlSession session = new SqlSession(dataSource);
		SessionFuture<SqlResult> sessionFuture = new SessionFuture<>(session);
		session.execute(sql, param).whenComplete((r, e) -> {
			if (e != null) {
				sessionFuture.completeExceptionally(e);
			} else {
				sessionFuture.complete(r);
			}
		});
		return sessionFuture;
	}

	/**
	 * 执行Sql
	 * 
	 * @param sql   sql语句
	 * @param param 参数
	 * @return 执行结果
	 */
	public <T> CompletableFuture<Integer> update(Sql<T> sql, T entity) {
		CompletableFuture<Integer> future = new CompletableFuture<>();
		try {
			this.execute(sql, sql.newParam().setEntity(entity)).finish((t, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(t.getInt());
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		return future;
	}

	/**
	 * 执行Sql
	 * 
	 * @param sql   sql语句
	 * @param param 参数
	 * @return 执行结果
	 */
	public <T> CompletableFuture<List<T>> query(Sql<T> sql, T entity) {
		CompletableFuture<List<T>> future = new CompletableFuture<List<T>>();
		try {
			this.execute(sql, sql.newParam().setEntity(entity)).finish((t, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(t.getList());
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		return future;
	}

	/**
	 * 执行Sql
	 * 
	 * @param sql   sql语句
	 * @param param 参数
	 * @return 执行结果
	 */
	public <T> CompletableFuture<List<T>> query(Sql<T> sql, QueryCondition qc) {
		CompletableFuture<List<T>> future = new CompletableFuture<List<T>>();
		try {
			this.execute(sql, sql.newParam()).finish((t, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(t.getList());
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		return future;
	}

	/**
	 * 执行Sql
	 * 
	 * @param sql   sql语句
	 * @param param 参数
	 * @return 执行结果
	 */
	public <T> CompletableFuture<Integer> count(Sql<T> sql, QueryCondition qc) {
		CompletableFuture<Integer> future = new CompletableFuture<Integer>();
		try {
			this.execute(sql, sql.newParam().setQuery(qc)).finish((t, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(t.getInt());
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		return future;
	}
}