package com.swak.async.execute;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.async.datasource.DataSource;
import com.swak.async.persistence.Sql;
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
	public TransactionContext beginQuery(TransactionContext context) {
		TransactionContext continueTransactionContext = context;
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
	public TransactionContext beginTransaction(TransactionContext context) {
		TransactionContext continueTransactionContext = context;
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
	private <T> SessionFuture<List<T>> execute(Sql<T> sql, T entity, QueryCondition query) {
		SqlSession session = new SqlSession(dataSource);
		SessionFuture<List<T>> sessionFuture = new SessionFuture<>(session);
		session.execute(sql, sql.newParam().setEntity(entity).setQuery(query)).whenComplete((r, e) -> {
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
	public <T> CompletableFuture<Void> update(Sql<T> sql, T entity) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		try {
			this.execute(sql, entity, null).finish((t, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(null);
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
			this.execute(sql, entity, null).finish((t, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(t);
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
			this.execute(sql, null, qc).finish((t, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(t);
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
	@SuppressWarnings("rawtypes")
	public <T> CompletableFuture<Integer> count(Sql<T> sql, QueryCondition qc) {
		CompletableFuture<Integer> future = new CompletableFuture<Integer>();
		try {
			this.execute(sql, null, qc).finish((t, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					try {
						List ts = (List) t;
						Integer count = ts != null && ts.size() > 0 ? (Integer) ts.get(0) : 0;
						future.complete(count);
					} catch (Exception ex) {
						future.completeExceptionally(ex);
					}
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		return future;
	}
}