package com.tmt.api.entity;

import java.io.Serializable;

/**
 * 商品
 * 
 * @author lifeng
 */
public class Goods implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String remarks;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}