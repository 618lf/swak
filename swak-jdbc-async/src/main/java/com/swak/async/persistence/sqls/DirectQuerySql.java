package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.SqlParam;

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

	public DirectQuerySql(String sql, List<Object> params, RowMapper<T> map) {
		this.sql = sql;
		this.params = params;
		this.map = map;
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		return sql;
	}

	@Override
	public List<Object> parseParams(SqlParam<T> param) {
		return params;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> RowMapper<U> rowMap() {
		return (RowMapper<U>) map;
	}

	/**
	 * 创建基本的参数
	 */
	@Override
	public SqlParam<T> newParam() {
		return new SqlParam<T>().setTable(null);
	}
}
