package com.swak.async.parameter;

import java.util.List;

import com.swak.async.persistence.define.ColumnDefine;
import com.swak.utils.Lists;

import io.vertx.sqlclient.Row;

/**
 * 测试
 * 
 * @author lifeng
 * @date 2020年10月14日 下午11:23:27
 */
public class ParaGetters {

	static List<ParaGetter> ParaGetters = Lists.newArrayList(3);

	static {
		ParaGetters.add(new DirectParaGetter());
		ParaGetters.add(new EnumGetter());
		ParaGetters.add(new BigDecimalGetter());
		ParaGetters.add(new DateGetter());
	}

	/**
	 * 转换为Java类型
	 * 
	 * @param rs
	 * @param column
	 * @return
	 */
	public static Object toJava(Row rs, ColumnDefine column) {
		for (ParaGetter paraGetter : ParaGetters) {
			if (paraGetter.support(column.javaField.getFieldClass())) {
				return paraGetter.toJava(rs, column);
			}
		}
		return null;
	}

	/**
	 * 转换为Jdbc类型
	 * 
	 * @param java
	 * @return
	 */
	public static Object toJdbc(Object java) {
		if (java != null) {
			for (ParaGetter paraGetter : ParaGetters) {
				if (paraGetter.support(java.getClass())) {
					return paraGetter.toJdbc(java);
				}
			}
		}
		return null;
	}

}
