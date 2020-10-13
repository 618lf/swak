package com.swak.async.persistence.sqls;

import java.util.List;
import java.util.Map;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;
import com.swak.utils.Lists;

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
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
				.append(this.parseTable(entity, query)).append(SPACE);
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());
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
}
