package com.swak.lombok;

import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Maps;
import com.swak.mongo.codec.BeanMaps;
import com.swak.utils.JsonMapper;

import net.sf.cglib.beans.BeanMap;

public class Test {

	/**
	 * 能正常的处理
	 * 
	 * @param data
	 */
	private static void beanUtilsTest_bean2bean() {
		Order2 data = new Order2();
		data.setId("111");
		data.setState(OrderState.开始);
		data.setType((byte)1);
		Order order = new Order();
		BeanUtils.copyProperties(data, order);
		System.out.println(JsonMapper.toJson(order));
	}

	/**
	 * 能正常的处理
	 * 
	 * @param data
	 */
	private static void beanUtilsTest_map2bean() {
		Map<String, Object> data = Maps.newHashMap();
		data.put("id", "111");
		data.put("state", OrderState.开始);
		data.put("type", 1);
		Order order = new Order();
		BeanUtils.copyProperties(data, order);
		System.out.println(JsonMapper.toJson(order));
	}

	/**
	 * 复制map 的属性到bean, 有一些问题（不适合在应用中使用）：<br>
	 * 1、这种方式和lombok有冲突: @Accessors(chain = true) <br>
	 * 2、自定义的枚举不能识别<br>
	 * 
	 */
	private static void cglibTest() {
		Map<String, Object> data = Maps.newHashMap();
		data.put("id", "111");
		data.put("state", OrderState.开始);
		data.put("type", 1);
		Order order = new Order();
		BeanMap beanMap = BeanMap.create(order);
		beanMap.putAll(data);
		System.out.println(JsonMapper.toJson(order));
	}

	/**
	 * 复制map 的属性到bean, 有一些问题（不适合在应用中使用）：<br>
	 * 1、这种方式和lombok有冲突: @Accessors(chain = true) <br>
	 * 2、自定义的枚举不能识别<br>
	 * 
	 */
	private static void swakTest() {
		Map<String, Object> data = Maps.newHashMap();
		data.put("id", "111");
		data.put("state", "开始");
		data.put("type", 1);
		Order order = BeanMaps.toBean(data, Order.class);
		System.out.println(JsonMapper.toJson(order));
	}

	public static void main(String[] args) {
		beanUtilsTest_bean2bean();
		beanUtilsTest_map2bean();
		cglibTest();
		swakTest();
	}
}
