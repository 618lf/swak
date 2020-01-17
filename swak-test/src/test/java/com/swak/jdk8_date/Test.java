package com.swak.jdk8_date;

import java.time.LocalDateTime;
import java.util.Map;

import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;

/**
 * Json 支持 jdk8 的日期
 * 
 * @author lifeng
 */
public class Test {

	public static void main(String[] args) {
		
		// json mapper 的支持
		Order order = new Order();
		order.setCreateDate(LocalDateTime.now());
		System.out.println(JsonMapper.toJson(order));
		
		// maps 的支持
		Map<String, Object> datas = Maps.newHashMap();
		datas.put("createDate", LocalDateTime.now());
		Maps.toBean(datas, order);
		System.out.println(JsonMapper.toJson(order));
	}
}