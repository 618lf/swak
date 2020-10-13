package com.swak.async.persistence.sqls;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;

/**
 * 查询
 * 
 * @author lifeng
 * @date 2020年10月8日 下午6:41:36
 */
public class QuerySql<T> extends BaseSql<T> implements Dql<T> {

	protected RowMapper<T> map;

	public QuerySql(TableDefine<T> table, RowMapper<T> map) {
		super(table);
		this.map = map;
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
				.append(this.parseTable()).append(SPACE);
		return sql.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> RowMapper<U> rowMap() {
		return (RowMapper<U>) map;
	}
}
