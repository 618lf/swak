package com.tmt.shop.entity;

import com.swak.common.entity.IdEntity;

public class Shop extends IdEntity<Long>{
	private static final long serialVersionUID = 1L;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
