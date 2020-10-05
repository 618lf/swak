package com.swak.persistence.async;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.persistence.QueryCondition;
import com.swak.persistence.dialect.Dialect;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

import net.sf.cglib.beans.BeanMap;

/**
 * 异步jdbc操作基类
 * 
 * @author lifeng
 * @date 2020年9月30日 下午8:25:54
 */
public class BaseAsyncJdbcDao {

	@Autowired
	private AsyncJdbcTemplate jdbcTemplate;
	@Autowired
	private Dialect dialect;

	/**
	 * 开启只读事务 -- 且必须手动提交
	 * 
	 * @return
	 */
	public TransactionContext beginQuery() {
		return jdbcTemplate.beginQuery(null);
	}

	/**
	 * 开启事务 -- 且必须手动提交
	 * 
	 * @return
	 */
	public TransactionContext beginTransaction() {
		return jdbcTemplate.beginTransaction(null);
	}

	/**
	 * 插入数据
	 * 
	 * @param sql
	 * @param param
	 */
	public CompletableFuture<TransactionContext> insert(TransactionContext context, String sql, Map<String, ?> param) {
		return context.update(sql, param);
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 * @param param
	 */
	public CompletableFuture<TransactionContext> update(TransactionContext context, String sql, Map<String, ?> param) {
		return context.update(sql, param);
	}

	/**
	 * 执行一条SQL
	 * 
	 * @param sql
	 */
	public CompletableFuture<TransactionContext> delete(TransactionContext context, String sql, Map<String, ?> param) {
		return context.update(sql, param);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <T> CompletableFuture<T> get(String sql, Map<String, ?> param, RowMapper<T> rowMapper) {
		return jdbcTemplate.query(sql, param, rowMapper).thenApply(datas -> {
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
	public <T> CompletableFuture<Page> page(String sql, QueryCondition qc, Parameters param, RowMapper<T> rowMapper) {

		// 转大小
		String valueSql = StringUtils.upperCase(sql);

		// 如果已经设置了 WHERE
		if (valueSql.endsWith("WHERE")) {
			valueSql = new StringBuilder(valueSql).append(" 1=1 ").append(qc.toString()).toString();
		} else {
			valueSql = new StringBuilder(valueSql).append(" WHERE 1=1 ").append(qc.toString()).toString();
		}

		// 排序条件
		if (StringUtils.isNotBlank(qc.getOrderByClause())) {
			valueSql = new StringBuilder(valueSql).append(" ORDER BY ").append(qc.getOrderByClause()).toString();
		}

		// 查询数量
		String countSql = new StringBuilder("SELECT COUNT(1) C FROM (").append(valueSql).append(")").toString();

		if (param.getPageIndex() == Parameters.NO_PAGINATION || param.getPageSize() == Parameters.NO_PAGINATION) {
			return jdbcTemplate.query(valueSql, rowMapper).thenApply(lst -> {
				return new Page(param, lst);
			});
		} else {
			return this.doPage(countSql, valueSql, param, rowMapper);
		}
	}

	private <T> CompletableFuture<Page> doPage(String countSql, String valueSql, Parameters param,
			RowMapper<T> rowMapper) {
		return this.beginQuery().count(countSql, Maps.newHashMap()).thenCompose(context -> {
			Integer count = context.getValue() == null ? 0 : context.getValue();
			param.setRecordCount(count);
			if (count == 0) {
				return CompletableFuture.completedFuture(context.setValue(Lists.newArrayList()));
			} else {
				int pageNum = param.getPageIndex();
				int pageSize = param.getPageSize();
				int pageCount = getPageCount(count, pageSize);
				if (pageNum > pageCount) {
					pageNum = pageCount;
				}
				return context.query(dialect.getLimitString(valueSql, (pageNum - 1) * pageSize, pageSize),
						Maps.newHashMap(), rowMapper);
			}

		}).thenCompose(context -> {
			return context.commit();
		}).thenApply(context -> {
			return new Page(param, context.getValue());
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
	public CompletableFuture<Integer> count(String sql, QueryCondition qc) {

		// 转大小
		String valueSql = StringUtils.upperCase(sql);

		// 如果已经设置了 WHERE
		if (valueSql.endsWith("WHERE")) {
			valueSql = new StringBuilder(valueSql).append(" 1=1 ").append(qc.toString()).toString();
		} else {
			valueSql = new StringBuilder(valueSql).append(" WHERE 1 = 1 ").append(qc.toString()).toString();
		}

		// 查询数量
		String countSql = new StringBuilder("SELECT COUNT(1) C FROM (").append(valueSql).append(")").toString();

		// 查询数量
		return jdbcTemplate.count(countSql, Maps.newHashMap());
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <T> CompletableFuture<List<T>> query(String sql, Map<String, ?> param, RowMapper<T> rowMapper) {
		return jdbcTemplate.query(sql, param, rowMapper);
	}

	/**
	 * 数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public CompletableFuture<Integer> count(String sql, Map<String, ?> param) {
		return jdbcTemplate.count(sql, param);
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

	/**
	 * 通过cglib 高效的转换
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> BeantoMap(Object bean) {
		Map<String, Object> map = Maps.newHashMap();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			Set<String> keys = beanMap.keySet();
			for (Object key : keys) {
				map.put(String.valueOf(key), beanMap.get(key));
			}
		}
		return map;
	}
}
