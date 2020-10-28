package com.swak.async.service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.App;
import com.swak.async.persistence.BaseDao;
import com.swak.async.tx.TransactionContext;
import com.swak.async.tx.TransactionalFuture;
import com.swak.entity.IdEntity;
import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.persistence.QueryCondition;

/**
 * 基础的服务
 * 
 * @author lifeng
 * @date 2020年10月8日 下午8:51:10
 */
public abstract class BaseService<T extends IdEntity<PK>, PK extends Serializable> {

	protected static Logger logger = LoggerFactory.getLogger(BaseService.class);

	/**
	 * 代理类
	 */
	protected Object proxy;

	/**
	 * 在子类实现此函数,为下面的CRUD操作提供DAO.
	 */
	protected abstract BaseDao<T, PK> getBaseDao();

	/**
	 * 获得代理类
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <U> U getProxy() {
		if (this.proxy == null) {
			Class<?>[] interfacess = this.getClass().getInterfaces();
			if (interfacess != null && interfacess.length > 0) {
				this.proxy = App.getBean(interfacess[0]);
			}
		}
		return (U) this.proxy;
	}

	/**
	 * 开启只读事务 -- 且必须手动提交
	 * 
	 * @return
	 */
	protected TransactionalFuture<Void> beginQuery() {
		return this.getBaseDao().beginQuery();
	}

	/**
	 * 开启事务 -- 且必须手动提交
	 * 
	 * @return
	 */
	protected TransactionalFuture<Void> beginTransaction() {
		return this.getBaseDao().beginTransaction();
	}

	// ******************** 事务的版本 ****************************

	/**
	 * 插入数据
	 * 
	 * @param sql
	 * @param param
	 */
	protected TransactionalFuture<T> insert(TransactionContext context, T entity) {
		return this.getBaseDao().insert(context, entity);
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 * @param param
	 */
	protected TransactionalFuture<Integer> update(TransactionContext context, T entity) {
		return this.getBaseDao().update(context, entity);
	}

	/**
	 * 删除数据
	 * 
	 * @param sql
	 */
	protected TransactionalFuture<Integer> delete(TransactionContext context, T entity) {
		return this.getBaseDao().delete(context, entity);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public TransactionalFuture<T> get(TransactionContext context, T entity) {
		return this.getBaseDao().get(context, entity);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public TransactionalFuture<List<T>> queryByCondition(TransactionContext context, QueryCondition qc) {
		return this.getBaseDao().queryByCondition(context, qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	protected TransactionalFuture<List<T>> query(TransactionContext context, String sql, QueryCondition qc) {
		return this.getBaseDao().query(context, sql, qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public TransactionalFuture<Integer> countByCondition(TransactionContext context, QueryCondition qc) {
		return this.getBaseDao().countByCondition(context, qc);
	}

	/**
	 * 数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	protected TransactionalFuture<Integer> count(TransactionContext context, String sql, QueryCondition qc) {
		return this.getBaseDao().count(context, sql, qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public TransactionalFuture<Page> pageByCondition(TransactionContext context, QueryCondition qc, Parameters param) {
		return this.getBaseDao().pageByCondition(context, qc, param);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	protected TransactionalFuture<Page> page(TransactionContext context, String sql, QueryCondition qc,
			Parameters param) {
		return this.getBaseDao().page(context, sql, qc, param);
	}

	// ******************** 无事务的版本 ****************************

	/**
	 * 插入数据
	 * 
	 * @param sql
	 * @param param
	 */
	protected CompletableFuture<PK> insert(T entity) {
		return this.getBaseDao().insert(entity);
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 * @param param
	 */
	protected CompletableFuture<Integer> update(T entity) {
		return this.getBaseDao().update(entity);
	}

	/**
	 * 删除数据
	 * 
	 * @param sql
	 */
	protected CompletableFuture<Integer> delete(T entity) {
		return this.getBaseDao().delete(entity);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public CompletableFuture<T> get(T entity) {
		return this.getBaseDao().get(entity);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public CompletableFuture<List<T>> queryByCondition(QueryCondition qc) {
		return this.getBaseDao().queryByCondition(qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	protected CompletableFuture<List<T>> query(String sql, QueryCondition qc) {
		return this.getBaseDao().query(sql, qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public CompletableFuture<Integer> countByCondition(QueryCondition qc) {
		return this.getBaseDao().countByCondition(qc);
	}

	/**
	 * 数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	protected CompletableFuture<Integer> count(String sql, QueryCondition qc) {
		return this.getBaseDao().count(sql, qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public CompletableFuture<Page> pageByCondition(QueryCondition qc, Parameters param) {
		return this.getBaseDao().pageByCondition(qc, param);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	protected CompletableFuture<Page> page(String sql, QueryCondition qc, Parameters param) {
		return this.getBaseDao().page(sql, qc, param);
	}
}
