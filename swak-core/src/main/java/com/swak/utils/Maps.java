package com.swak.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.swak.cache.LRUCache;
import com.swak.codec.Encodes;

import net.sf.cglib.beans.BeanMap;

/**
 * Maps 操作类
 *
 * @author: lifeng
 * @date: 2020/3/29 14:24
 */
public class Maps {

	/**
	 * 获取第一个元素
	 *
	 * @param map 集合
	 * @return 第一组元素
	 */
	public static <K, V> V getFirst(Map<K, V> map) {
		if (map.isEmpty()) {
			return null;
		}
		return map.entrySet().iterator().next().getValue();
	}

	/**
	 * 创建一个Map 默认大小 16
	 *
	 * @return HashMap
	 */
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<>(16);
	}

	/**
	 * 创建一个Map
	 *
	 * @param size 大小
	 * @return HashMap
	 */
	public static <K, V> HashMap<K, V> newHashMap(int size) {
		return new HashMap<>(size);
	}

	/**
	 * 创建一个有序的Map
	 *
	 * @return LinkedHashMap
	 */
	public static <K, V> LinkedHashMap<K, V> newOrderMap() {
		return new LinkedHashMap<>();
	}

	/**
	 * 创建一个固定容量的Lru 容器
	 *
	 * @return LRUCache
	 */
	public static <K, V> Map<K, V> newLRUCache(int maxCapacity) {
		return new LRUCache<>(maxCapacity);
	}

	/**
	 * 计算大小
	 *
	 * @param expectedSize 期望的大小
	 * @return 实际的大小
	 */
	public static int capacity(int expectedSize) {
		int limit = 3;
		if (expectedSize < limit) {
			return expectedSize + 1;
		}
		if (expectedSize < Ints.MAX_POWER_OF_TWO) {
			return expectedSize + expectedSize / 3;
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * 将 bean 转为 map, 忽略为 null 的属性（效率稍低）
	 *
	 * @param bean bean
	 * @return Map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> fromBean(Object bean) {
		String json = JsonMapper.toJson(bean);
		return JsonMapper.fromJson(json, Map.class);
	}

	/**
	 * 通过cglib 高效的转换
	 *
	 * @param bean bean
	 * @return Map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object bean) {
		Map<String, Object> map = Maps.newHashMap();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			Set<String> keys = beanMap.keySet();
			for (Object key : keys) {
				if (beanMap.get(key) != null) {
					map.put(String.valueOf(key), beanMap.get(key));
				}
			}
		}
		return map;
	}

	/**
	 * 复制map 的属性到bean, 有一些问题（不适合在应用中使用）：<br>
	 * 1、这种方式和lombok有冲突: @Accessors(chain = true) <br>
	 * 2、自定义的枚举不能识别<br>
	 *
	 * @param map  map
	 * @param bean bean
	 * @return bean
	 */
	public static <T> T toBean(Map<String, Object> map, T bean) {
		BeanMap beanMap = BeanMap.create(bean);
		beanMap.putAll(map);
		return bean;
	}

	/**
	 * Map key 排序
	 *
	 * @param maps maps
	 * @return Map
	 */
	public static Map<String, Object> sort(Map<String, Object> maps) {
		HashMap<String, Object> tempMap = new LinkedHashMap<>();
		List<Map.Entry<String, Object>> infoIds = new ArrayList<>(maps.entrySet());

		infoIds.sort(Map.Entry.comparingByKey());

		for (Map.Entry<String, Object> item : infoIds) {
			tempMap.put(item.getKey(), item.getValue());
		}
		return tempMap;
	}

	/**
	 * url 参数串连
	 *
	 * @param map map
	 * @return String
	 */
	public static String join(Map<String, Object> map) {
		return join(map, false, false);
	}

	/**
	 * url 参数串连
	 *
	 * @param map            map
	 * @param keyLower       是否小写
	 * @param valueUrlencode value是否编码
	 * @return String
	 */
	public static String join(Map<String, Object> map, boolean keyLower, boolean valueUrlencode) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String key : map.keySet()) {
			if (map.get(key) != null && !"".equals(map.get(key))) {
				String temp = (key.endsWith("_") && key.length() > 1) ? key.substring(0, key.length() - 1) : key;
				stringBuilder.append(keyLower ? temp.toLowerCase() : temp).append("=").append(
						valueUrlencode ? Encodes.urlEncode(map.get(key).toString()).replace("+", "%20") : map.get(key))
						.append("&");
			}
		}
		if (stringBuilder.length() > 0) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		return stringBuilder.toString();
	}

	/**
	 * XML格式字符串转换为Map
	 *
	 * @param xmlStr xmlStr 字符串
	 * @return Map
	 */
	public static Map<String, Object> fromXml(String xmlStr) {
		Map<String, Object> data = Maps.newHashMap();
		Document doc = XmlParse.parse(xmlStr);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getDocumentElement().getChildNodes();
		for (int idx = 0; idx < nodeList.getLength(); ++idx) {
			Node node = nodeList.item(idx);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				org.w3c.dom.Element element = (org.w3c.dom.Element) node;
				data.put(element.getNodeName(), element.getTextContent());
			}
		}
		return data;
	}
}
