package com.swak.param;

import java.util.List;

public class Param {

	private String name;

	private List<ParamItem> items;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ParamItem> getItems() {
		return items;
	}

	public void setItems(List<ParamItem> items) {
		this.items = items;
	}
}
