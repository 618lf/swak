package com.swak.async.persistence.sqls;

import java.util.List;
import java.util.Map;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;
import com.swak.utils.Lists;

/**
 * 添加脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 上午12:05:05
 */
public class UpdateSql<T> extends BaseSql<T> implements Dml<T> {

	public UpdateSql(TableDefine<T> table) {
		super(table);
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(UPDATE).append(SPACE).append(this.parseTable(entity, query)).append(SPACE).append(SET).append(SPACE);
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
	public List<Object> parseParams(T entity, QueryCondition query) {
		List<ColumnDefine> columns = this.table.columns;
		List<Object> params = Lists.newArrayList(columns.size());
		if (entity != null) {
			try {
				Map<String, Object> maps = this.BeantoMap(entity);
				for (ColumnDefine column : columns) {
					Object value = maps.get(column.javaProperty);
					params.add(value);
				}
			} catch (Exception e) {
			}
		}
		return params;
	}

	@Override
	public <U> RowMapper<U> rowMap() {
		return null;
	}
}
