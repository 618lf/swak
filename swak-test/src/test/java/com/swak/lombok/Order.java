package com.swak.lombok;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {

	private String id;
	private String name;
	private OrderState state;
}