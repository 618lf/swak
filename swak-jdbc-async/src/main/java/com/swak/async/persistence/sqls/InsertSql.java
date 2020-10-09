package com.swak.async.persistence.sqls;

import com.swak.async.persistence.define.TableDefine;

/**
 * 插入SQL
 * 
 * @author lifeng
 * @date 2020年10月7日 下午11:31:12
 */
public class InsertSql<T> extends BaseSql<T> {

	public InsertSql(TableDefine<T> table) {
		super(table);
	}

	@Override
	protected String parseScript() {
		// SQL 脚本
		StringBuilder sql = new StringBuilder();
		sql.append(INSERT).append(SPACE).append(this.parseTable()).append(LEFT_KH);
		sql.append(this.parseColumns()).append(RIGHT_KH);
		sql.append(VALUES).append(LEFT_KH).append(this.parseInsertParams()).append(RIGHT_KH);
		return sql.toString();
	}
}
