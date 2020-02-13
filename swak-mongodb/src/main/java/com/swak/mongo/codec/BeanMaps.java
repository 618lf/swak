package com.swak.mongo.codec;

import java.util.Map;
import java.util.Set;

import com.swak.mongo.json.Document;
import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

/**
 * Bean 和 Map 的相互转换
 * 
 * @author lifeng
 */
public class BeanMaps {

	/**
	 * Bean to Map
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object bean) {
		Map<String, Object> map = Maps.newHashMap();
		if (bean != null) {
			net.sf.cglib.beans.BeanMap beanMap = net.sf.cglib.beans.BeanMap.create(bean);
			Set<String> keys = beanMap.keySet();
			for (Object key : keys) {
				Object value = beanMap.get(key);
				if (value != null) {
					if ((value instanceof String && StringUtils.isNotBlank((String) value))) {
						continue;
					}
					map.put(String.valueOf(key), beanMap.get(key));
				}
			}
		}
		return map;
	}

	/**
	 * Map to Bean
	 * 
	 * @param doc
	 * @return
	 */
	public static <T> T toBean(Document doc, Class<T> clazz) {
		String json = JsonMapper.toJson(doc);
		return JsonMapper.fromJson(json, clazz);
	}
}
