package com.swak.async.persistence.sqls;

import java.util.List;
import java.util.Map;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;
import com.swak.utils.Lists;

/**
 * 插入SQL
 * 
 * @author lifeng
 * @date 2020年10月7日 下午11:31:12
 */
public class InsertSql<T> extends BaseSql<T> implements Dml<T> {

	public InsertSql(TableDefine<T> table) {
		super(table);
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(INSERT).append(SPACE).append(this.parseTable(entity, query)).append(LEFT_KH);
		sql.append(this.parseColumns()).append(RIGHT_KH);
		sql.append(VALUES).append(LEFT_KH).append(this.parseInsertParams()).append(RIGHT_KH);
		return sql.toString();
	}

	protected String parseInsertParams() {
		StringBuilder sql = new StringBuilder();
		for (int i = 0; i < this.table.columns.size(); i++) {
			sql.append(OCCUPIED).append(SPLIT);
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
