package com.swak.fields;

import java.util.List;

import com.swak.utils.JsonMapper;

/**
 * 测试的订单
 * 
 * @author lifeng
 */
public class Order {

	private Long id;
	private List<OrderItem> item;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<OrderItem> getItem() {
		return item;
	}

	public void setItem(List<OrderItem> item) {
		this.item = item;
	}

	public void setItem(String items) {
		this.item = JsonMapper.fromJsonToList(items, OrderItem.class);
	}
}
