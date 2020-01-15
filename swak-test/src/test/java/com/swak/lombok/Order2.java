package com.swak.lombok;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Order2 {

	private String id;
	private String name;
}