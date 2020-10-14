package com.swak.async.parameter;

import java.math.BigDecimal;

import com.swak.async.persistence.define.ColumnDefine;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.data.Numeric;

/**
 * BigDecimal转换
 * 
 * @author lifeng
 * @date 2020年10月14日 下午11:13:59
 */
public class BigDecimalGetter implements ParaGetter {

	@Override
	public boolean support(Class<?> fieldClass) {
		return fieldClass.isAssignableFrom(BigDecimal.class);
	}

	@Override
	public Object toJava(Row rs, ColumnDefine column) {
		int pos = rs.getColumnIndex(column.name);
		Numeric numeric = rs.get(Numeric.class, pos);
		if (!(numeric == null || numeric.isNaN())) {
			return numeric.bigDecimalValue();
		}
		return null;
	}

	@Override
	public Object toJdbc(Object java) {
		BigDecimal value = (BigDecimal) java;
		return value != null ? Numeric.create(value) : null;
	}
}
