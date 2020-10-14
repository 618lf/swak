package com.swak.async.persistence.maps;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.async.parameter.ParaGetters;
import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.Sql;
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

	protected static Logger logger = LoggerFactory.getLogger(Sql.class);

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
				Object value = ParaGetters.toJava(rs, column);
				column.javaField.getMethod().invoke(t, value);
			}
		} catch (Exception e) {
			logger.error("解析结果错误：", e);
		}
		return t;
	}
}