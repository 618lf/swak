package com.swak.lombok;

import org.springframework.beans.BeanUtils;

public class Test {

	public static void main(String[] args) {

		Order2 data = new Order2();
		data.setId("111");

		// 如果不添加 @Accessors(chain = true) 则没问题，添加之后不能正常处理
		Order order = new Order();

		BeanUtils.copyProperties(data, order);
		System.out.println(order.getId());
	}
}
