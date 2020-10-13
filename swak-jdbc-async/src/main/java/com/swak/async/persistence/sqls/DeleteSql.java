package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.define.TableDefine;

/**
 * 删除脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 下午4:28:10
 */
public class DeleteSql<T> extends ShardingSql<T> implements Dml<T> {

	public DeleteSql(TableDefine<T> table) {
		super(table);
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE).append(SPACE).append(FROM).append(SPACE).append(TABLE).append(SPACE)
				.append(this.parseTable(param)).append(SPACE);
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());
		return sql.toString();
	}

	/**
	 * 解析参数
	 */
	@Override
	public List<Object> parseParams(SqlParam<T> param) {
		return param.getEntityIdValues();
	}

	@Override
	public <U> RowMapper<U> rowMap() {
		return null;
	}
}