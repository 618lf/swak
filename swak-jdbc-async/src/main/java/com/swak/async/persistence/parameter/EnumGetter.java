package com.swak.async.persistence.parameter;

import com.swak.async.persistence.define.ColumnDefine;

import io.vertx.sqlclient.Row;

/**
 * 枚举转换
 * 
 * @author lifeng
 * @date 2020年10月14日 下午11:21:24
 */
public class EnumGetter implements ParaGetter {

	@Override
	public boolean support(Class<?> fieldClass) {
		return fieldClass.isEnum();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object toJava(Row rs, ColumnDefine column) {
		int pos = rs.getColumnIndex(column.name);
		String string = rs.get(String.class, pos);
		if (string != null) {
			return Enum.valueOf((Class) column.javaField.getFieldClass(), string);
		}
		return null;
	}

	@Override
	public Object toJdbc(Object java) {
		return java != null ? java.toString() : null;
	}
}
