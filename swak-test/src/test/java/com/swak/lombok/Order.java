package com.swak.lombok;

import lombok.Data;

@Data
public class Order {

	private String id;
	private String name;
	private OrderState state;
	private Byte type;
}