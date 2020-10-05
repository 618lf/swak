package com.swak.persistence.async;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.swak.utils.Lists;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

/**
 * 事务上下文
 * 
 * @author lifeng
 * @date 2020年9月30日 下午10:14:26
 */
public class TransactionContext {

	/**
	 * 事务上下文
	 */
	private TransactionContextImmutability context;

	/**
	 * 当前值
	 */
	private Object value;

	/**
	 * 错误
	 */
	private Throwable error;

	/**
	 * 固定属性
	 * 
	 * @param context
	 */
	private TransactionContext(Throwable error) {
		this.error = error;
	}

	/**
	 * 固定属性
	 * 
	 * @param context
	 */
	private TransactionContext(TransactionContextImmutability context) {
		this.context = context;
	}

	/**
	 * 创建一个事务上下文
	 * 
	 * @param pool
	 */
	private TransactionContext(Pool pool, boolean readOnly) {
		this.context = new TransactionContextImmutability();
		this.context.pool = pool;
		this.context.readOnly = readOnly;
		try {
			this.context.pool.getConnection((res) -> {
				if (res.cause() != null) {
					this.context.prepared.completeExceptionally(res.cause());
				} else {
					this.context.connection = res.result();
					this.context.begin();
				}
			});
		} catch (Exception e) {
			this.context.prepared.completeExceptionally(e);
		}
	}

	/**
	 * 开启事务
	 */
	protected CompletableFuture<Void> prepared() {

		// 已经提交直接抛出异常
		if (this.context.commited.get()) {
			throw new RuntimeException("Tx already commit!");
		}

		// 异常处理
		if (this.error != null) {
			throw new RuntimeException(error);
		}

		// 获取连接
		return this.context.prepared;
	}

	/**
	 * 执行Sql
	 * 
	 * @param sql   sql语句
	 * @param param 参数
	 * @return 执行结果
	 */
	public CompletableFuture<TransactionContext> update(String sql, Map<String, ?> param) {
		CompletableFuture<TransactionContext> future = this.context.future();
		try {
			this.prepared().whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					this.context.channel().preparedQuery(sql).execute(Tuple.wrap(param.values()), (res) -> {
						if (res.cause() != null) {
							future.completeExceptionally(res.cause());
						} else {
							future.complete(this.next().setValue(null));
						}
					});
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
	public <T> CompletableFuture<TransactionContext> query(String sql, Map<String, ?> param, RowMapper<T> rowMapper) {
		CompletableFuture<TransactionContext> future = this.context.future();
		try {
			this.prepared().whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					this.context.channel().preparedQuery(sql).execute(Tuple.wrap(param.values()), (res) -> {
						if (res.cause() != null) {
							future.completeExceptionally(res.cause());
						} else {
							this.rowMappers(future, res.result(), rowMapper);
						}
					});
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
	public <T> CompletableFuture<TransactionContext> count(String sql, Map<String, ?> param) {
		CompletableFuture<TransactionContext> future = this.context.future();
		try {
			this.prepared().whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					this.context.channel().preparedQuery(sql).execute(Tuple.wrap(param.values()), (res) -> {
						if (res.cause() != null) {
							future.completeExceptionally(res.cause());
						} else {
							this.rowMappers(future, res.result(), (row, i) -> {
								return row.getInteger(1);
							});
						}
					});
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		return future.thenApply(tc -> {
			List<Integer> ls = tc.getValue();
			return tc.setValue(ls != null && ls.size() > 0 ? ls.get(0) : 0);
		});
	}

	private <T> void rowMappers(CompletableFuture<TransactionContext> future, RowSet<Row> rows,
			RowMapper<T> rowMapper) {
		try {
			List<T> ts = Lists.newArrayList();
			RowIterator<Row> datas = rows.iterator();
			int rowNum = 0;
			while (datas.hasNext()) {
				Row row = datas.next();
				ts.add(rowMapper.mapRow(row, rowNum));
				rowNum++;
			}
			future.complete(this.next().setValue(ts));
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
	}

	/**
	 * 提交
	 * 
	 * @return
	 */
	public CompletableFuture<TransactionContext> commit() {
		CompletableFuture<TransactionContext> future = new CompletableFuture<>();
		if (this.context.commited.compareAndSet(false, true)) {
			this.context.commit().whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(this);
				}
			});
		} else {
			future.complete(this);
		}
		return future;
	}

	/**
	 * 是否已提交
	 * 
	 * @return
	 */
	public boolean isCommited() {
		return this.context.commited.get();
	}

	/**
	 * 下一步
	 * 
	 * @return
	 */
	private TransactionContext next() {
		return new TransactionContext(this.context);
	}

	/**
	 * 返回当前值
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) value;
	}

	/**
	 * 设置值
	 * 
	 * @param value
	 * @return
	 */
	public TransactionContext setValue(Object value) {
		this.value = value;
		return this;
	}

	/**
	 * 开启事务
	 * 
	 * @param pool
	 * @return
	 */
	public static TransactionContext of(Pool pool, boolean readOnly) {
		return new TransactionContext(pool, readOnly);
	}

	/**
	 * 错误
	 * 
	 * @param error
	 * @return
	 */
	public static TransactionContext error(Throwable error) {
		return new TransactionContext(error);
	}
}
