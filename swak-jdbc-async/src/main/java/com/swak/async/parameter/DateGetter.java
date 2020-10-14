package com.swak.async.parameter;

import java.time.LocalDateTime;
import java.util.Date;

import com.swak.async.persistence.define.ColumnDefine;
import com.swak.utils.time.DateTimes;

import io.vertx.sqlclient.Row;

public class DateGetter implements ParaGetter {

	@Override
	public boolean support(Class<?> fieldClass) {
		return fieldClass.isAssignableFrom(Date.class);
	}

	@Override
	public Object toJava(Row rs, ColumnDefine column) {
		int pos = rs.getColumnIndex(column.name);
		LocalDateTime dateTime = rs.get(LocalDateTime.class, pos);
		if (!(dateTime == null)) {
			return DateTimes.convertLDTToDate(dateTime);
		}
		return null;
	}

	@Override
	public Object toJdbc(Object java) {
		Date value = (Date) java;
		return value != null ? DateTimes.convertDateToLDT(value) : null;
	}
}
