package com.swak.async.persistence.sqls;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;

/**
 * 删除脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 下午4:28:10
 */
public class ExistsSql<T> extends CountSql<T> {

	public ExistsSql(TableDefine<T> table, RowMapper<Integer> map) {
		super(table, map);
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(COUNT).append(SPACE).append(FROM).append(SPACE)
				.append(this.parseTable()).append(SPACE);
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());
		return sql.toString();
	}
}