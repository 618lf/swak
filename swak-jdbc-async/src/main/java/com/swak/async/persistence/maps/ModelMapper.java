package com.swak.async.persistence.maps;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.Sql;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;
import com.swak.utils.time.DateTimes;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.data.Numeric;

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
				int pos = rs.getColumnIndex(column.name);
				this.handlePos(t, rs, pos, column);
			}
		} catch (Exception e) {
			logger.error("解析结果错误：", e);
		}
		return t;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void handlePos(T t, Row rs, int pos, ColumnDefine column)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// 数据类型
		Class<?> fieldClass = column.javaField.getFieldClass();

		// 对应的值
		Object value = null;

		// 无需转换的字段
		if (fieldClass.isAssignableFrom(Byte.class) || fieldClass.isAssignableFrom(Short.class)
				|| fieldClass.isAssignableFrom(Integer.class) || fieldClass.isAssignableFrom(Long.class)
				|| fieldClass.isAssignableFrom(Double.class) || fieldClass.isAssignableFrom(Float.class)
				|| fieldClass.isAssignableFrom(Numeric.class) || fieldClass.isAssignableFrom(LocalDate.class)
				|| fieldClass.isAssignableFrom(Duration.class) || fieldClass.isAssignableFrom(LocalDateTime.class)) {

			value = rs.get(fieldClass, pos);
			column.javaField.getMethod().invoke(t, value);
		}

		// 处理BigDecimal
		else if (fieldClass.isAssignableFrom(BigDecimal.class)) {
			Numeric numeric = rs.get(Numeric.class, pos);
			if (!(numeric == null || numeric.isNaN())) {
				value = numeric.bigDecimalValue();
			}
		}

		// 处理Date
		else if (fieldClass.isAssignableFrom(Date.class)) {
			LocalDateTime dateTime = rs.get(LocalDateTime.class, pos);
			if (!(dateTime == null)) {
				value = DateTimes.convertLDTToDate(dateTime);
			}
		}

		// 处理枚举
		else if (fieldClass.isEnum()) {
			String string = rs.get(String.class, pos);
			if (string != null) {
				value = Enum.valueOf((Class) fieldClass, string);
			}
		}

		// 设置值
		column.javaField.getMethod().invoke(t, value);
	}
}