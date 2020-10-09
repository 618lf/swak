package com.swak.async.persistence.define;

import com.swak.asm.FieldCache.FieldMeta;

/**
 * 列定义
 * 
 * @author lifeng
 * @date 2020年10月8日 上午12:12:32
 */
public class ColumnDefine {

	/**
	 * 表名称
	 */
	public String name;

	/**
	 * 属性
	 */
	public String javaProperty;

	/**
	 * Java 类型
	 */
	public FieldMeta javaField;

	/**
	 * 定义列
	 * 
	 * @return
	 */
	public static ColumnDefine of(String name, String javaProperty, FieldMeta field) {
		ColumnDefine column = new ColumnDefine();
		column.name = name;
		column.javaProperty = javaProperty;
		column.javaField = field;
		return column;
	}
}