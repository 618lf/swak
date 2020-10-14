package com.swak.async.tx;

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
	public <T> TransactionalFuture update(Sql<T> sql, T entity) {
		TransactionalFuture future = new TransactionalFuture();
		try {
			this.execute(sql, entity, null).whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(this.next().setValue(null));
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
	public <T> TransactionalFuture query(Sql<T> sql, T entity) {
		TransactionalFuture future = new TransactionalFuture();
		try {
			this.execute(sql, entity, null).whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(this.next().setValue(r.getList()));
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
	public <T> TransactionalFuture query(Sql<T> sql, QueryCondition qc) {
		TransactionalFuture future = new TransactionalFuture();
		try {
			this.execute(sql, null, qc).whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(this.next().setValue(r.getList()));
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
	public <T> TransactionalFuture count(Sql<T> sql, QueryCondition qc) {
		TransactionalFuture future = new TransactionalFuture();
		try {
			this.execute(sql, null, qc).whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(this.next().setValue(r.getInt()));
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		return future;
	}

	/**
	 * 结束事务
	 * 
	 * @return
	 */
	public TransactionalFuture finish(Throwable error) {
		// 自定义的回滚异常
		if (error != null && this.context.rollbackFor != null && this.context.rollbackFor.length > 0) {
			for (int i = 0; i < this.context.rollbackFor.length; i++) {
				Class<? extends Throwable> e = this.context.rollbackFor[i];
				if (error.getClass().isAssignableFrom(e)) {
					return this.rollback();
				}
			}
		}
		// 默认的回滚异常
		else if (error != null && error.getClass().isAssignableFrom(RuntimeException.class)) {
			return this.rollback();
		}
		// 提交
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
			this.context.session.commit().whenComplete((r, e) -> {
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
			this.context.session.rollback().whenComplete((r, e) -> {
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
	public TransactionContext setRollbackFor(Class<? extends Throwable>[] exs) {
		this.context.rollbackFor = exs;
		return this;
	}

	/**
	 * 开启事务
	 * 
	 * @param pool
	 * @return
	 */
	public static TransactionContext of(SqlSession session, boolean readOnly) {
		return new TransactionContext(session, readOnly);
	}

}
