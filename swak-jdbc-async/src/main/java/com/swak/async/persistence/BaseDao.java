package com.swak.async.persistence;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.swak.async.persistence.define.SqlMap;
import com.swak.async.persistence.sqls.CountSql;
import com.swak.async.persistence.sqls.DeleteSql;
import com.swak.async.persistence.sqls.GetSql;
import com.swak.async.persistence.sqls.InsertSql;
import com.swak.async.persistence.sqls.QuerySql;
import com.swak.async.persistence.sqls.UpdateSql;
import com.swak.async.tx.TransactionContext;
import com.swak.async.tx.TransactionalFuture;
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
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private Dialect dialect;

	/**
	 * 开启只读事务 -- 且必须手动提交
	 * 
	 * @return
	 */
	public TransactionalFuture beginQuery() {
		return TransactionalFuture.completedFuture(jdbcTemplate.beginQuery(null));
	}

	/**
	 * 开启事务 -- 且必须手动提交
	 * 
	 * @return
	 */
	public TransactionalFuture beginTransaction() {
		return TransactionalFuture.completedFuture(jdbcTemplate.beginTransaction(null));
	}

	// ******************** 事务的版本 ****************************

	/**
	 * 插入数据
	 * 
	 * @param sql
	 * @param param
	 */
	@SuppressWarnings("unchecked")
	public TransactionalFuture insert(TransactionContext context, T entity) {
		final PK pk = entity instanceof IdEntity ? ((IdEntity<PK>) entity).prePersist() : null;
		InsertSql<T> sql = this.getSql(SqlMap.INSERT);
		return context.update(sql.script(), sql.parse(entity)).txApply(ctx -> ctx.setValue(pk));
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 * @param param
	 */
	public TransactionalFuture update(TransactionContext context, T entity) {
		UpdateSql<T> sql = this.getSql(SqlMap.UPDATE);
		return context.update(sql.script(), sql.parse(entity));
	}

	/**
	 * 删除数据
	 * 
	 * @param sql
	 */
	public TransactionalFuture delete(TransactionContext context, T entity) {
		DeleteSql<T> sql = this.getSql(SqlMap.DELETE);
		return context.update(sql.script(), sql.parse(entity));
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public TransactionalFuture get(TransactionContext context, T entity) {
		GetSql<T> sql = this.getSql(SqlMap.GET);
		return context.query(sql.script(), sql.parse(entity), sql.getMapper()).txApply(ctx -> {
			List<T> datas = ctx.getValue();
			return ctx.setValue(datas != null && datas.size() >= 1 ? datas.get(0) : null);
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
	public TransactionalFuture queryByCondition(TransactionContext context, QueryCondition qc) {
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
	public TransactionalFuture query(TransactionContext context, String sql, QueryCondition qc) {
		QuerySql<T> querySql = this.getSql(sql);
		return context.query(querySql.parseScriptWithCondition(qc), Lists.newArrayList(), querySql.getMapper());
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public TransactionalFuture countByCondition(TransactionContext context, QueryCondition qc) {
		return this.count(context, SqlMap.COUNT, qc);
	}

	/**
	 * 数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public TransactionalFuture count(TransactionContext context, String sql, QueryCondition qc) {
		CountSql<T> querySql = this.getSql(sql);
		return context.count(querySql.parseScriptWithCondition(qc), Lists.newArrayList());
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public TransactionalFuture pageByCondition(TransactionContext context, QueryCondition qc, Parameters param) {
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
	public TransactionalFuture page(TransactionContext context, String sql, QueryCondition qc, Parameters param) {
		if (param.getPageIndex() == Parameters.NO_PAGINATION || param.getPageSize() == Parameters.NO_PAGINATION) {
			QuerySql<T> querySql = this.getSql(sql);
			return context.query(querySql.parseScriptWithCondition(qc), Lists.newArrayList(), querySql.getMapper())
					.txApply(ctx -> {
						return ctx.setValue(new Page(param, ctx.getValue()));
					});
		}
		return this.doPage(context, sql, qc, param);
	}

	private TransactionalFuture doPage(TransactionContext ctx, String sql, QueryCondition qc, Parameters param) {
		QuerySql<T> querySql = this.getSql(sql);
		CountSql<T> countSql = this.getSql(sql + "Stat");
		return ctx.count(countSql.parseScriptWithCondition(qc), Lists.newArrayList()).txCompose(context -> {
			Integer count = context.getValue() == null ? 0 : context.getValue();
			param.setRecordCount(count);
			if (count == 0) {
				return TransactionalFuture.completedFuture(context.setValue(Lists.newArrayList()));
			} else {
				int pageNum = param.getPageIndex();
				int pageSize = param.getPageSize();
				int pageCount = getPageCount(count, pageSize);
				if (pageNum > pageCount) {
					pageNum = pageCount;
				}
				return context.query(dialect.getLimitString(querySql.parseScriptWithCondition(qc),
						(pageNum - 1) * pageSize, pageSize), Lists.newArrayList(), querySql.getMapper());
			}

		}).txApply(context -> {
			return context.setValue(new Page(param, context.getValue()));
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
		return jdbcTemplate.update(sql.script(), sql.parse(entity)).thenApply(res -> pk);
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 * @param param
	 */
	public CompletableFuture<Void> update(T entity) {
		UpdateSql<T> sql = this.getSql(SqlMap.UPDATE);
		return jdbcTemplate.update(sql.script(), sql.parse(entity));
	}

	/**
	 * 删除数据
	 * 
	 * @param sql
	 */
	public CompletableFuture<Void> delete(T entity) {
		DeleteSql<T> sql = this.getSql(SqlMap.DELETE);
		return jdbcTemplate.update(sql.script(), sql.parse(entity));
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
		return jdbcTemplate.query(sql.script(), sql.parse(entity), sql.getMapper()).thenApply(datas -> {
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
		return jdbcTemplate.query(querySql.parseScriptWithCondition(qc), Lists.newArrayList(), querySql.getMapper());
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
		return jdbcTemplate.count(querySql.parseScriptWithCondition(qc), Lists.newArrayList());
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
			return jdbcTemplate.query(querySql.parseScriptWithCondition(qc), querySql.getMapper()).thenApply(lst -> {
				return new Page(param, lst);
			});
		}
		return this.doPage(sql, qc, param);
	}

	private CompletableFuture<Page> doPage(String sql, QueryCondition qc, Parameters param) {
		QuerySql<T> querySql = this.getSql(sql);
		CountSql<T> countSql = this.getSql(sql + "Stat");
		return this.beginQuery().txCompose(context -> {
			return context.count(countSql.parseScriptWithCondition(qc), Lists.newArrayList());
		}).txCompose(context -> {
			Integer count = context.getValue() == null ? 0 : context.getValue();
			param.setRecordCount(count);
			if (count == 0) {
				return TransactionalFuture.completedFuture(context.setValue(Lists.newArrayList()));
			} else {
				int pageNum = param.getPageIndex();
				int pageSize = param.getPageSize();
				int pageCount = getPageCount(count, pageSize);
				if (pageNum > pageCount) {
					pageNum = pageCount;
				}
				return context.query(dialect.getLimitString(querySql.parseScriptWithCondition(qc),
						(pageNum - 1) * pageSize, pageSize), Lists.newArrayList(), querySql.getMapper());
			}

		}).finish(context -> {
			return new Page(param, context.getValue());
		});
	}

	/**
	 * page count
	 * 
	 * @param recordCount
	 * @param pageSize
	 * @return
	 */
	private int getPageCount(int recordCount, int pageSize) {
		if (recordCount == 0)
			return 0;
		return recordCount % pageSize > 0 ? ((recordCount / pageSize) + 1) : (recordCount / pageSize);
	}
}
