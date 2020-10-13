package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.persistence.QueryCondition;

/**
 * 直接查询Sql
 * 
 * @author lifeng
 * @date 2020年10月12日 下午8:51:30
 */
public class DirectQuerySql<T> extends ExecuteSql<T> implements Dql<T> {

	private String sql;
	private List<Object> params;
	private RowMapper<T> map;
	protected boolean transaction;

	public DirectQuerySql(String sql, List<Object> params, RowMapper<T> map) {
		this.sql = sql;
		this.params = params;
		this.map = map;
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		return sql;
	}

	@Override
	public List<Object> parseParams(T entity, QueryCondition query) {
		return params;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> RowMapper<U> rowMap() {
		return (RowMapper<U>) map;
	}
}
