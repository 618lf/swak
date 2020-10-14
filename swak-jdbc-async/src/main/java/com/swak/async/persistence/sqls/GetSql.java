package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.define.TableDefine;

/**
 * 通过主键查询
 * 
 * @author lifeng
 * @date 2020年10月8日 下午2:24:17
 */
public class GetSql<T> extends QuerySql<T> {

	public GetSql(TableDefine<T> table, RowMapper<T> map) {
		super(table, map);
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
				.append(this.parseTable(param)).append(SPACE);
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());
		return this.parseQuery(sql.toString(), param.query);
	}

	@Override
	public List<Object> parseParams(SqlParam<T> param) {
		return param.getEntityIdValues();
	}
}
