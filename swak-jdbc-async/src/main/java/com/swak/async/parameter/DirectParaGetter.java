package com.swak.async.parameter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.swak.async.persistence.define.ColumnDefine;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.data.Numeric;

/**
 * 这些类型目前是系统直接支持的
 * 
 * @author lifeng
 * @date 2020年10月14日 下午10:38:48
 */
public class DirectParaGetter implements ParaGetter {

	@Override
	public boolean support(Class<?> fieldClass) {
		return fieldClass.isAssignableFrom(String.class) || fieldClass.isAssignableFrom(Byte.class)
				|| fieldClass.isAssignableFrom(Short.class) || fieldClass.isAssignableFrom(Integer.class)
				|| fieldClass.isAssignableFrom(Long.class) || fieldClass.isAssignableFrom(Double.class)
				|| fieldClass.isAssignableFrom(Float.class) || fieldClass.isAssignableFrom(Numeric.class)
				|| fieldClass.isAssignableFrom(LocalDate.class) || fieldClass.isAssignableFrom(Duration.class)
				|| fieldClass.isAssignableFrom(LocalDateTime.class);
	}

	@Override
	public Object toJava(Row rs, ColumnDefine column) {
		int pos = rs.getColumnIndex(column.name);
		return rs.get(column.javaField.getFieldClass(), pos);
	}

	@Override
	public Object toJdbc(Object java) {
		return java;
	}
}
