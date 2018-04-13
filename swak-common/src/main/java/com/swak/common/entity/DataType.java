package com.swak.common.entity;

/**
 * 数据类型
 * @author root
 */
public enum DataType{
	STRING("文本"), NUMBER("数字"), DATE("日期"), BOOLEAN("布尔"), MONEY("金额");
	private String name;
	private DataType( String name ) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}