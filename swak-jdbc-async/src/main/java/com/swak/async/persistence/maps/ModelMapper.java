package com.swak.async.persistence.maps;

import java.sql.SQLException;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;

import io.vertx.sqlclient.Row;

/**
 * 模型映射器
 * 
 * @author lifeng
 * @date 2020年10月8日 下午7:01:39
 */
public class ModelMapper<T> implements RowMapper<T> {

	TableDefine<T> table;

	public ModelMapper(TableDefine<T> table) {
		this.table = table;
	}

	@Override
	public T mapRow(Row rs, int rowNum) throws SQLException {
		T t = null;
		try {

			// 创建实例
			t = this.table.entity.newInstance();

			// 属性处理
			for (ColumnDefine column : this.table.columns) {
				int pos = rs.getColumnIndex(column.name);
				Object value = rs.get(column.javaField.getFieldClass(), pos);
				column.javaField.getMethod().invoke(t, value);
			}
		} catch (Exception e) {
		}
		return t;
	}
}