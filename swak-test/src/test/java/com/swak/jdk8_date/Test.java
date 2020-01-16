package com.swak.jdk8_date;

import java.time.LocalDateTime;

import com.swak.utils.JsonMapper;

/**
 * Json 支持 jdk8 的日期
 * 
 * @author lifeng
 */
public class Test {

	public static void main(String[] args) {
		Order order = new Order();
		order.setCreateDate(LocalDateTime.now());
		System.out.println(JsonMapper.toJson(order));
	}
}