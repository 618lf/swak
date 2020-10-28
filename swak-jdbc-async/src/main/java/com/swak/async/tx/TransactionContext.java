package com.swak.async.tx;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.async.execute.SqlSession;
import com.swak.async.persistence.Sql;
import com.swak.async.persistence.SqlResult;
import com.swak.persistence.QueryCondition;

/**
 * 事务上下文
 * 
 * @author lifeng
 * @date 2020年9月30日 下午10:14:26
 */
public class TransactionContext<U> {

	/**
	 * 事务上下文
	 */
	private TransactionContextImmutability context;

	/**
	 * 当前值
	 */
	private U value;

	/**
	 * 错误
	 */
	private Throwable error;

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
	private TransactionContext(SqlSession session, boolean readOnly) {
		this.context = new TransactionContextImmutability();
		this.context.session = session;
		this.context.readOnly = readOnly;
	}

	/**
	 * 进入
	 * 
	 * @return
	 */
	TransactionContext<U> acquire() {
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
	 * 执行Sql
	 */
	protected <T> CompletableFuture<SqlResult> execute(Sql<T> sql, T entity, QueryCondition query) {

		// 已经提交直接抛出异常
		if (this.context.commited.get()) {
			throw new RuntimeException("Tx already commit!");
		}

		// 获取连接
		return this.context.session.execute(sql, this.context.readOnly,
				sql.newParam().setEntity(entity).setQuery(query));
	}

	/**
	 * 执行Sql
	 * 
	 * @param sql   sql语句
	 * @param param 参数
	 * @return 执行结果
	 */
	public <T> TransactionalFuture<Integer> update(Sql<T> sql, T entity) {
		TransactionalFuture<Integer> future = new TransactionalFuture<>();
		try {
			this.execute(sql, entity, null).whenComplete((r, e) -> {
				if (e != null) {
					future.completeValue(this, e, null);
				} else {
					future.completeValue(this, null, r.getInt());
				}
			});
		} catch (Exception e) {
			future.completeValue(this, e, null);
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
	public <T> TransactionalFuture<List<T>> query(Sql<T> sql, T entity) {
		TransactionalFuture<List<T>> future = new TransactionalFuture<>();
		try {
			this.execute(sql, entity, null).whenComplete((r, e) -> {
				if (e != null) {
					future.completeValue(this, e, null);
				} else {
					future.completeValue(this, null, r.getList());
				}
			});
		} catch (Exception e) {
			future.completeValue(this, e, null);
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
	public <T> TransactionalFuture<List<T>> query(Sql<T> sql, QueryCondition qc) {
		TransactionalFuture<List<T>> future = new TransactionalFuture<>();
		try {
			this.execute(sql, null, qc).whenComplete((r, e) -> {
				if (e != null) {
					future.completeValue(this, e, null);
				} else {
					future.completeValue(this, null, r.getList());
				}
			});
		} catch (Exception e) {
			future.completeValue(this, e, null);
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
	public <T> TransactionalFuture<Integer> count(Sql<T> sql, QueryCondition qc) {
		TransactionalFuture<Integer> future = new TransactionalFuture<>();
		try {
			this.execute(sql, null, qc).whenComplete((r, e) -> {
				if (e != null) {
					future.completeValue(this, e, null);
				} else {
					future.completeValue(this, null, r.getInt());
				}
			});
		} catch (Exception e) {
			future.completeValue(this, e, null);
		}
		return future;
	}

	/**
	 * 结束事务
	 * 
	 * @return
	 */
	public TransactionalFuture<Void> finish(Throwable ex) {

		// 错误
		Throwable error = ex != null ? ex : this.error;

		// 自定义的回滚异常
		if (error != null && this.context.rollbackFor != null && this.context.rollbackFor.length > 0) {
			for (int i = 0; i < this.context.rollbackFor.length; i++) {
				Class<? extends Throwable> e = this.context.rollbackFor[i];
				if (error.getClass().isAssignableFrom(e)) {
					return this.rollback(error);
				}
			}
		}
		// 默认的回滚异常
		else if (error != null && error.getClass().isAssignableFrom(RuntimeException.class)) {
			return this.rollback(error);
		}
		// 提交
		return this.commit(error);
	}

	/**
	 * 提交 -- 需要将错误抛出到前端
	 * 
	 * @return
	 */
	private TransactionalFuture<Void> commit(Throwable error) {
		TransactionalFuture<Void> future = new TransactionalFuture<>();
		if (this.context.commited.compareAndSet(false, true)) {
			this.context.session.commit().whenComplete((r, e) -> {
				if (e != null || error != null) {
					future.completeExceptionally(e != null ? e : error);
				} else {
					future.completeValue(this, null, null);
				}
			});
		} else {
			future.completeValue(this, null, null);
		}
		return future;
	}

	/**
	 * 回滚 -- 需要将错误抛出到前端
	 * 
	 * @return
	 */
	private TransactionalFuture<Void> rollback(Throwable error) {
		TransactionalFuture<Void> future = new TransactionalFuture<>();
		if (this.context.commited.compareAndSet(false, true)) {
			this.context.session.rollback().whenComplete((r, e) -> {
				if (e != null || error != null) {
					future.completeExceptionally(e != null ? e : error);
				} else {
					future.completeValue(this, null, null);
				}
			});
		} else {
			future.completeValue(this, null, null);
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
	<T> TransactionContext<T> nextU() {
		return new TransactionContext<T>(this.context);
	}

	/**
	 * 返回当前值
	 * 
	 * @return
	 */
	public U getValue() {
		return value;
	}

	/**
	 * 返回错误
	 * 
	 * @return
	 */
	public Throwable getError() {
		return error;
	}

	/**
	 * 设置当时值
	 * 
	 * @param value 当时值
	 * @return
	 */
	public TransactionContext<U> setValue(U value) {
		this.value = value;
		return this;
	}

	/**
	 * 设置错误
	 * 
	 * @param error 错误
	 * @return
	 */
	public TransactionContext<U> setError(Throwable error) {
		this.error = error;
		return this;
	}

	/**
	 * 封装成任务
	 * 
	 * @return
	 */
	public TransactionalFuture<Void> toFuture() {
		return TransactionalFuture.completedFuture(this, null);
	}

	/**
	 * 设置需要回滚的异常
	 * 
	 * @param ex
	 * @return
	 */
	public TransactionContext<U> setRollbackFor(Class<? extends Throwable>[] exs) {
		this.context.rollbackFor = exs;
		return this;
	}

	/**
	 * 开启事务
	 * 
	 * @param pool
	 * @return
	 */
	public static <T> TransactionContext<T> of(SqlSession session, boolean readOnly) {
		return new TransactionContext<T>(session, readOnly);
	}

}
