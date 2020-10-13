package com.swak.async.persistence.sqls;

import java.util.List;
import java.util.Map;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;
import com.swak.utils.Lists;

/**
 * 删除脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 下午4:28:10
 */
public class LockSql<T> extends BaseSql<T> implements Dml<T> {

	RowMapper<T> map;

	public LockSql(TableDefine<T> table, RowMapper<T> map) {
		super(table);
		this.map = map;
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
				.append(this.parseTable(entity, query)).append(SPACE).append(WHERE).append(SPACE)
				.append(this.parseEqualsIdParams()).append(SPACE).append(LOCK);
		return sql.toString();
	}

	/**
	 * 解析参数
	 */
	@Override
	public List<Object> parseParams(T entity, QueryCondition query) {
		List<ColumnDefine> pks = this.table.getPkColumns();
		List<Object> params = Lists.newArrayList(pks.size());
		if (entity != null) {
			try {
				Map<String, Object> maps = this.BeantoMap(entity);
				for (ColumnDefine column : pks) {
					Object value = maps.get(column.javaProperty);
					params.add(value);
				}
			} catch (Exception e) {
			}
		}
		return params;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> RowMapper<U> rowMap() {
		return (RowMapper<U>) map;
	}
}