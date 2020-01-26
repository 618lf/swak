package com.swak.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.persistence.dialect.Dialect;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

/**
 * 简单的基于JDBC的dao实现
 * 
 * @author lifeng
 */
public class BaseJdbcDao {

	// 数据库相关的设置
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	private Dialect dialect;

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <T> T get(String sql, Map<String, ?> param, RowMapper<T> rowMapper) {
		return jdbcTemplate.queryForObject(sql, param, rowMapper);
	}
	
	/**
	 * 插入数据
	 * 
	 * @param sql
	 * @param param
	 */
	public void insert(String sql, Map<String, ?> param) {
		jdbcTemplate.update(sql, param);
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 * @param param
	 */
	public void update(String sql, Map<String, ?> param) {
		jdbcTemplate.update(sql, param);
	}

	/**
	 * 执行一条SQL
	 * 
	 * @param sql
	 */
	public void delete(String sql, Map<String, ?> param) {
		jdbcTemplate.update(sql, param);
	}

	/**
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <T> Page page(String sql, QueryCondition qc, Parameters param, RowMapper<T> rowMapper) {

		// 转大小
		String valueSql = StringUtils.upperCase(sql);

		// 如果已经设置了 WHERE
		if (valueSql.endsWith("WHERE")) {
			valueSql = new StringBuilder(valueSql).append(" 1=1 ").append(qc.toString()).toString();
		} else {
			valueSql = new StringBuilder(valueSql).append(" ").append(qc.toString()).toString();
		}

		// 查询数量
		String countSql = new StringBuilder("SELECT COUNT(1) C FROM (").append(valueSql).append(")").toString();

		// 分页参数设置
		int pageNum = param.getPageIndex();
		int pageSize = param.getPageSize();
		Integer count = 0;
		List<T> lst = null;
		if (pageNum == Parameters.NO_PAGINATION || pageSize == Parameters.NO_PAGINATION) {
			lst = jdbcTemplate.query(valueSql, rowMapper);
		} else {
			count = jdbcTemplate.queryForObject(countSql, Maps.newHashMap(), Integer.class);
			count = count == null ? 0 : count;
			if (count == 0) {
				lst = Lists.newArrayList();
			} else {
				int pageCount = getPageCount(count, pageSize);
				if (pageNum > pageCount) {
					pageNum = pageCount;
				}
				lst = jdbcTemplate.query(dialect.getLimitString(valueSql, (pageNum - 1) * pageSize, pageSize),
						rowMapper);
			}
		}
		param.setRecordCount(count);
		return new Page(param, lst);
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
	 * 结果
	 * 
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public <T> List<T> query(String sql, Map<String, ?> param, RowMapper<T> rowMapper) {
		return jdbcTemplate.query(sql, param, rowMapper);
	}

	/**
	 * 数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public Integer count(String sql, Map<String, ?> param) {
		return jdbcTemplate.queryForObject(sql, param, Integer.class);
	}
}
