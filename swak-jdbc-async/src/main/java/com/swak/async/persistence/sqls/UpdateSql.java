package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;
import com.swak.meters.MetricsFactory;

/**
 * 添加脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 上午12:05:05
 */
public class UpdateSql<T> extends ShardingSql<T> implements Dml<T> {

	RowMapper<Integer> map;

	public UpdateSql(TableDefine<T> table, RowMapper<Integer> map, MetricsFactory metricsFactory) {
		super(table, metricsFactory);
		this.map = map;
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		StringBuilder sql = new StringBuilder();
		sql.append(UPDATE).append(SPACE).append(this.parseTable(param)).append(SPACE).append(SET).append(SPACE);
		sql.append(this.parseUpdateParams());
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());
		return sql.toString();
	}

	/**
	 * 解析插入列
	 */
	protected String parseUpdateParams() {
		StringBuilder sql = new StringBuilder();
		for (ColumnDefine column : this.table.columns) {
			if (!column.isPk()) {
				String name = column.name;
				sql.append(name).append(EQUALS).append(OCCUPIED).append(SPLIT);
			}
		}
		if (this.table.hasColumn()) {
			sql.delete(sql.lastIndexOf(SPLIT), sql.length() - 1);
		}
		return sql.toString();
	}

	@Override
	public List<Object> parseParams(SqlParam<T> param) {
		return param.getEntityValues();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> RowMapper<U> rowMap() {
		return (RowMapper<U>) map;
	}
}
