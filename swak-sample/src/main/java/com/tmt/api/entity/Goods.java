package com.tmt.api.entity;

import java.io.Serializable;

import com.swak.entity.IdEntity;
import com.swak.incrementer.IdGen;

/**
 * 商品
 * 
 * @author lifeng
 */
public class Goods extends IdEntity<String> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String remarks;

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

	@Override
	public String prePersist() {
		Long id = IdGen.id();
		this.setId(id.toString());
		return this.id;
	}

}