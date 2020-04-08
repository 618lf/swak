package com.tmt.api.web;

/**
 * 子数据
 * 
 * @author lifeng
 */
public class ParamItem {

	private String name;
	private Integer size;
	private Param param;

	public Param getParam() {
		return param;
	}

	public void setParam(Param param) {
		this.param = param;
	}

	public String getName() {
		return name;
	}

	public Integer getSize() {
		return size;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name=").append(name).append(";");
		sb.append("size=").append(size).append(";");
		return sb.toString();
	}
}