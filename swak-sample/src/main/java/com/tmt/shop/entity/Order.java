package com.tmt.shop.entity;

import com.swak.entity.IdEntity;

/**
 * 订单
 * 
 * @author lifeng
 */
public class Order extends IdEntity<Long>{
	private static final long serialVersionUID = 1L;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
