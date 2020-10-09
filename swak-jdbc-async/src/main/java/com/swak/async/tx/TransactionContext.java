package com.swak.async.tx;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.async.persistence.RowMapper;
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
	 * 引用次数 -- 使用事务注解时才需要用到
	 */
	private AtomicInteger reference;

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
	 * 进入
	 * 
	 * @return
	 */
	TransactionContext acquire() {
		if (this.reference == null) {
			this.reference = new AtomicInteger(1);
		} else {
			this.reference.incrementAndGet();
		}
		return this;
	}

	/**
	 * 释放
	 * 
	 * @return
	 */
	boolean released() {
		if (this.reference != null) {
			this.reference.decrementAndGet();
		}
		return this.reference == null || this.reference.get() <= 0;
	}

	/**
	 * 开启事务
	 */
	protected CompletableFuture<Void> prepared() {

		// 已经提交直接抛出异常
		if (this.context.commited.get()) {
			throw new RuntimeException("Tx already commit!");
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
	public TransactionalFuture update(String sql, List<Object> params) {
		TransactionalFuture future = this.context.future();
		try {
			this.prepared().whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					this.context.channel().preparedQuery(sql).execute(Tuple.wrap(params), (res) -> {
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
	public <T> TransactionalFuture query(String sql, List<Object> params, RowMapper<T> rowMapper) {
		TransactionalFuture future = this.context.future();
		try {
			this.prepared().whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					this.context.channel().preparedQuery(sql).execute(Tuple.wrap(params), (res) -> {
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
	public <T> TransactionalFuture count(String sql, List<Object> params) {
		TransactionalFuture future = this.context.future();
		try {
			this.prepared().whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					this.context.channel().preparedQuery(sql).execute(Tuple.wrap(params), (res) -> {
						if (res.cause() != null) {
							future.completeExceptionally(res.cause());
						} else {
							this.classMapper(future, res.result(), Integer.class);
						}
					});
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		return future;
	}

	private <T> void classMapper(CompletableFuture<TransactionContext> future, RowSet<Row> rows, Class<T> mapperClass) {
		try {
			Object value = null;
			RowIterator<Row> datas = rows.iterator();
			if (datas.hasNext()) {
				Row row = datas.next();
				if (Integer.class.isAssignableFrom(mapperClass)) {
					value = row.getInteger(1);
				} else if (Long.class.isAssignableFrom(mapperClass)) {
					value = row.getLong(1);
				}
			}
			future.complete(this.next().setValue(value));
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
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
	 * 结束事务
	 * 
	 * @return
	 */
	public TransactionalFuture finish(Throwable error) {
		if (error != null && this.context.rollbackFor != null
				&& error.getClass().isAssignableFrom(this.context.rollbackFor)) {
			return this.rollback();
		}
		return this.commit();
	}

	/**
	 * 提交
	 * 
	 * @return
	 */
	private TransactionalFuture commit() {
		TransactionalFuture future = new TransactionalFuture();
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
	 * 回滚
	 * 
	 * @return
	 */
	private TransactionalFuture rollback() {
		TransactionalFuture future = new TransactionalFuture();
		if (this.context.commited.compareAndSet(false, true)) {
			this.context.rollback().whenComplete((r, e) -> {
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
	 * 设置当时值
	 * 
	 * @param value
	 * @return
	 */
	public TransactionContext setValue(Object value) {
		this.value = value;
		return this;
	}

	/**
	 * 封装成任务
	 * 
	 * @return
	 */
	public TransactionalFuture toFuture() {
		return TransactionalFuture.completedFuture(this);
	}

	/**
	 * 设置需要回滚的异常
	 * 
	 * @param ex
	 * @return
	 */
	public TransactionContext setRollbackFor(Class<Throwable> ex) {
		this.context.rollbackFor = ex;
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

}
