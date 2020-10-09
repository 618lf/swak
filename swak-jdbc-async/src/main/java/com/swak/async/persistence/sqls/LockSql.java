package com.swak.async.persistence.sqls;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.TableDefine;

/**
 * 删除脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 下午4:28:10
 */
public class LockSql<T> extends QuerySql<T> {

	public LockSql(TableDefine<T> table, RowMapper<T> map) {
		super(table, map);
	}

	@Override
	protected String parseScript() {

		// sql 语句
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
				.append(this.parseTable()).append(SPACE);

		// 拼接条件
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams()).append(SPACE);

		// 锁住记录
		sql.append(LOCK);

		// 返回Sql 语句
		return sql.toString();
	}
}