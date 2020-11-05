package com.swak.async.persistence;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.swak.async.persistence.define.SqlMap;
import com.swak.async.persistence.execute.SqlExecuter;
import com.swak.async.persistence.sqls.CountSql;
import com.swak.async.persistence.sqls.DeleteSql;
import com.swak.async.persistence.sqls.GetSql;
import com.swak.async.persistence.sqls.InsertSql;
import com.swak.async.persistence.sqls.PageSql;
import com.swak.async.persistence.sqls.QuerySql;
import com.swak.async.persistence.sqls.UpdateSql;
import com.swak.async.persistence.tx.TransactionContext;
import com.swak.async.persistence.tx.TransactionalFuture;
import com.swak.entity.IdEntity;
import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.persistence.QueryCondition;
import com.swak.persistence.dialect.Dialect;
import com.swak.utils.Lists;

/**
 * 异步jdbc操作基类
 * 
 * @author lifeng
 * @date 2020年9月30日 下午8:25:54
 */
public class BaseDao<T, PK> extends ModelRegister<T, PK> {

	protected static Logger logger = LoggerFactory.getLogger(BaseDao.class);

	@Autowired
	protected SqlExecuter sqlExecuter;
	@Autowired
	protected Dialect dialect;

	/**
	 * 开启只读事务 -- 且必须手动提交
	 * 
	 * @return
	 */
	public TransactionalFuture<Void> beginQuery() {
		return TransactionalFuture.completedFuture(sqlExecuter.beginQuery(null), null);
	}

	/**
	 * 开启事务 -- 且必须手动提交
	 * 
	 * @return
	 */
	public TransactionalFuture<Void> beginTransaction() {
		return TransactionalFuture.completedFuture(sqlExecuter.beginTransaction(null), null);
	}

	// ******************** 事务的版本 ****************************

	/**
	 * 插入数据
	 * 
	 * @param sql
	 * @param param
	 */
	@SuppressWarnings("unchecked")
	public <U> TransactionalFuture<T> insert(TransactionContext<U> context, T entity) {
		if (entity instanceof IdEntity) {
			((IdEntity<PK>) entity).prePersist();
		}
		InsertSql<T> sql = this.getSql(SqlMap.INSERT);
		return context.update(sql, entity).txApply(ctx -> entity);
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 * @param param
	 */
	public <U> TransactionalFuture<Integer> update(TransactionContext<U> context, T entity) {
		UpdateSql<T> sql = this.getSql(SqlMap.UPDATE);
		return context.update(sql, entity);
	}

	/**
	 * 删除数据
	 * 
	 * @param sql
	 */
	public <U> TransactionalFuture<Integer> delete(TransactionContext<U> context, T entity) {
		DeleteSql<T> sql = this.getSql(SqlMap.DELETE);
		return context.update(sql, entity);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <U> TransactionalFuture<T> get(TransactionContext<U> context, T entity) {
		GetSql<T> sql = this.getSql(SqlMap.GET);
		return context.query(sql, entity).txApply(ctx -> {
			List<T> datas = ctx.getValue();
			return datas != null && datas.size() >= 1 ? datas.get(0) : null;
		});
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <U> TransactionalFuture<List<T>> queryByCondition(TransactionContext<U> context, QueryCondition qc) {
		return this.query(context, SqlMap.QUERY, qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <U> TransactionalFuture<List<T>> query(TransactionContext<U> context, String sql, QueryCondition qc) {
		QuerySql<T> querySql = this.getSql(sql);
		return context.query(querySql, qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <U> TransactionalFuture<Integer> countByCondition(TransactionContext<U> context, QueryCondition qc) {
		return this.count(context, SqlMap.COUNT, qc);
	}

	/**
	 * 数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public <U> TransactionalFuture<Integer> count(TransactionContext<U> context, String sql, QueryCondition qc) {
		CountSql<T> querySql = this.getSql(sql);
		return context.count(querySql, qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <U> TransactionalFuture<Page> pageByCondition(TransactionContext<U> context, QueryCondition qc,
			Parameters param) {
		return this.page(context, SqlMap.QUERY, qc, param);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <U> TransactionalFuture<Page> page(TransactionContext<U> context, String sql, QueryCondition qc,
			Parameters param) {
		if (param.getPageIndex() == Parameters.NO_PAGINATION || param.getPageSize() == Parameters.NO_PAGINATION) {
			QuerySql<T> querySql = this.getSql(sql);
			return context.query(querySql, qc).txApply(ctx -> {
				return new Page(param, ctx.getValue());
			});
		}
		return this.doPage(context, sql, qc, param);
	}

	private <U> TransactionalFuture<Page> doPage(TransactionContext<U> ctx, String sql, QueryCondition qc,
			Parameters param) {
		QuerySql<T> querySql = this.getSql(sql);
		CountSql<T> countSql = this.getSql(sql + "Stat");
		return ctx.count(countSql, qc).txCompose(context -> {
			Integer count = context.getValue() == null ? 0 : context.getValue();
			param.setRecordCount(count);
			if (count > 0) {
				return context.query(new PageSql<>(querySql, dialect, param), qc);
			}
			return TransactionalFuture.completedFuture(context, Lists.newArrayList());
		}).txApply(context -> {
			return new Page(param, context.getValue());
		});
	}

	// ******************** 无事务的版本 ****************************

	/**
	 * 插入数据
	 * 
	 * @param sql
	 * @param param
	 */
	@SuppressWarnings("unchecked")
	public CompletableFuture<PK> insert(T entity) {
		final PK pk = entity instanceof IdEntity ? ((IdEntity<PK>) entity).prePersist() : null;
		InsertSql<T> sql = this.getSql(SqlMap.INSERT);
		return sqlExecuter.update(sql, entity).thenApply(res -> pk);
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 * @param param
	 */
	public CompletableFuture<Integer> update(T entity) {
		UpdateSql<T> sql = this.getSql(SqlMap.UPDATE);
		return sqlExecuter.update(sql, entity);
	}

	/**
	 * 删除数据
	 * 
	 * @param sql
	 */
	public CompletableFuture<Integer> delete(T entity) {
		DeleteSql<T> sql = this.getSql(SqlMap.DELETE);
		return sqlExecuter.update(sql, entity);
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
		GetSql<T> sql = this.getSql(SqlMap.GET);
		return sqlExecuter.query(sql, entity).thenApply(datas -> {
			return datas != null && datas.size() >= 1 ? datas.get(0) : null;
		});
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
		return this.query(SqlMap.QUERY, qc);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public CompletableFuture<List<T>> query(String sql, QueryCondition qc) {
		QuerySql<T> querySql = this.getSql(sql);
		return sqlExecuter.query(querySql, qc);
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
		return this.count(SqlMap.COUNT, qc);
	}

	/**
	 * 数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public CompletableFuture<Integer> count(String sql, QueryCondition qc) {
		CountSql<T> querySql = this.getSql(sql);
		return sqlExecuter.count(querySql, qc);
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
		return this.page(SqlMap.QUERY, qc, param);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public CompletableFuture<Page> page(String sql, QueryCondition qc, Parameters param) {
		if (param.getPageIndex() == Parameters.NO_PAGINATION || param.getPageSize() == Parameters.NO_PAGINATION) {
			QuerySql<T> querySql = this.getSql(sql);
			return sqlExecuter.query(querySql, qc).thenApply(lst -> {
				return new Page(param, lst);
			});
		}
		return this.doPage(sql, qc, param);
	}

	private CompletableFuture<Page> doPage(String sql, QueryCondition qc, Parameters param) {
		QuerySql<T> querySql = this.getSql(sql);
		CountSql<T> countSql = this.getSql(sql + "Stat");
		return this.beginQuery().txCompose(context -> {
			return context.count(countSql, qc);
		}).txCompose(context -> {
			Integer count = context.getValue() == null ? 0 : context.getValue();
			param.setRecordCount(count);
			if (count > 0) {
				return context.query(new PageSql<>(querySql, dialect, param), qc);
			}
			return TransactionalFuture.completedFuture(context, Lists.newArrayList());
		}).finish(context -> {
			return new Page(param, context.getValue());
		});
	}
}
