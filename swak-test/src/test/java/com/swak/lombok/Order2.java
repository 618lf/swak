package com.swak.lombok;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order2 {

	private String id;
	private String name;
	private OrderState state;
}