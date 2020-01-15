package com.swak.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.swak.codec.Encodes;

import net.sf.cglib.beans.BeanMap;

/**
 * Maps 操作类
 * 
 * @author lifeng
 */
public class Maps {

	/**
	 * 获取第一个元素
	 * 
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
	 * 
	 * @return
	 */
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	/**
	 * 创建一个Map
	 * 
	 * @return
	 */
	public static <K, V> HashMap<K, V> newHashMap(int size) {
		return new HashMap<K, V>(size);
	}

	/**
	 * 创建一个有序的Map
	 * 
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
	 * 
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
	 * 复制map 的属性到bean, 这种方式和lombok有冲突: @Accessors(chain = true)
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

	/**
	 * Map key 排序
	 * 
	 * @param maps
	 * @return
	 */
	public static Map<String, Object> sort(Map<String, Object> maps) {
		HashMap<String, Object> tempMap = new LinkedHashMap<String, Object>();
		List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(maps.entrySet());

		Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
			public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		for (int i = 0; i < infoIds.size(); i++) {
			Map.Entry<String, Object> item = infoIds.get(i);
			tempMap.put(item.getKey(), item.getValue());
		}
		return tempMap;
	}

	/**
	 * url 参数串连
	 * 
	 * @param map
	 * @param keyLower
	 * @param valueUrlencode
	 * @return
	 */
	public static String join(Map<String, Object> map) {
		return join(map, false, false);
	}

	/**
	 * url 参数串连
	 * 
	 * @param map
	 * @param keyLower
	 * @param valueUrlencode
	 * @return
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
	 * @param xmlStr
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> fromXml(String xmlStr) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
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
		} catch (Exception ex) {
			throw ex;
		}
	}
}
