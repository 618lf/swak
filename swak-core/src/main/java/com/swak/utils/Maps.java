package com.swak.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.cglib.beans.BeanMap;

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
	 * 将 bean 转为 map, 忽略为 null 的属性（效率稍低）
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> fromBean(Object bean) {
		String json = JsonMapper.toJson(bean);
		return JsonMapper.fromJson(json, Map.class);
	}
	
	/**
	 * 通过cglib 高效的转换
	 * 
	 * @param bean
	 * @return
	 */
	public static Map<String, Object> toMap(Object bean) {
		Map<String, Object> map = Maps.newHashMap();  
	    if (bean != null) {  
	        BeanMap beanMap = BeanMap.create(bean);  
	        for (Object key : beanMap.keySet()) {  
	            map.put(String.valueOf(key), beanMap.get(key));  
	        }             
	    }  
	    return map;
	}
	
	/**
	 * 复制map 的属性到bean
	 * 
	 * @param map
	 * @param bean
	 * @return
	 */
	public static <T> T toBean(Map<String, Object> map, T bean) {
		BeanMap beanMap = BeanMap.create(bean);  
	    beanMap.putAll(map);  
	    return bean;
	}
}
