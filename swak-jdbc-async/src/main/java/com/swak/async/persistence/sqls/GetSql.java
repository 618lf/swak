package com.swak.async.persistence.sqls;

import com.swak.async.persistence.RowMapper;
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
	protected String parseScript() {

		// sql 语句
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
				.append(this.parseTable()).append(SPACE);

		// 拼接条件
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());

		// 返回Sql 语句
		return sql.toString();
	}
}
