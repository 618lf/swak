package com.swak.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps 操作类
 * 
 * @author lifeng
 */
public class Maps {
	
	/**
	 * 获取第一个元素
	 * @param map
	 * @return
	 */
	public static <K, V> V getFirst(Map<K, V> map) {
		if (map.isEmpty()) {
			return null;
		}
		return map.entrySet().iterator().next().getValue();
	}

	/**
	 * 创建一个Map
	 * @return
	 */
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	/**
	 * 创建一个有序的Map
	 * @return
	 */
	public static <K, V> LinkedHashMap<K, V> newOrderMap() {
		return new LinkedHashMap<K, V>();
	}

	public static int capacity(int expectedSize) {
		if (expectedSize < 3) {
			return expectedSize + 1;
		}
		if (expectedSize < Ints.MAX_POWER_OF_TWO) {
			return expectedSize + expectedSize / 3;
		}
		return Integer.MAX_VALUE;
	}
	
	/**
	 * 将 bean 转为 map, 忽略为 null 的属性
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> fromBean(Object bean) {
		String json = JsonMapper.toJson(bean);
		return JsonMapper.fromJson(json, Map.class);
	}
}
