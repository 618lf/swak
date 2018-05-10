package com.tmt.shop.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="shop")
public class ShopXml {

	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
