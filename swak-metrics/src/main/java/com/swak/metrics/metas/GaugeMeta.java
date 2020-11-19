package com.swak.metrics.metas;

/**
 * 指标数据转换
 * 
 * @author lifeng
 */
public class GaugeMeta {

	private String type;
	private String name;
	private String value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
