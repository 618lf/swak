package com.sample.tools.plugin.plugins.codegen.gen;

import java.util.List;

/**
 * 表
 * 
 * @author lifeng
 */
public class Table {

	private String name;
	private List<TableColumn> columns;

	public List<TableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<TableColumn> columns) {
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPkJavaType() {
		if (this.columns != null && this.columns.size() != 0) {
			for (TableColumn column : columns) {
				if (1 == column.getIsPk()) {
					return column.getJavaType();
				}
			}
		}
		return "String";// 默认string
	}

	/**
	 * 第一个string类型的字段
	 * 
	 * @return
	 */
	public String getFisrtStringField() {
		if (this.columns != null && this.columns.size() != 0) {
			for (TableColumn column : columns) {
				if ("String".equals(column.getJavaType()) && !column.getIsBaseEntity()) {
					return column.getJavaField();
				}
			}
		}
		return "name";
	}

	/**
	 * 是否有日期
	 * 
	 * @return
	 */
	public Boolean getHasDate() {
		if (this.columns != null && this.columns.size() != 0) {
			for (TableColumn column : columns) {
				if ("java.util.Date".equals(column.getJavaType()) && !column.getIsBaseEntity()) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
}