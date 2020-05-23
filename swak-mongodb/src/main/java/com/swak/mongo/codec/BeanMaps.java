package com.swak.mongo.codec;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;
import com.swak.asm.FieldCache.FieldMeta;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

import net.sf.cglib.beans.BeanMap;

/**
 * Bean 和 Map 的相互转换
 * 
 * @author lifeng
 */
public class BeanMaps {

	/**
	 * Beans to Map
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toCascadeMap(Object bean, boolean nullAble) {
		Map<String, Object> values = BeanMaps.toMap(bean, nullAble);
		values.entrySet().forEach((e) -> {
			Object value = e.getValue();
			if (value != null && value instanceof List) {
				List<Object> os = (List<Object>) value;
				List<Object> _os = null;
				if (os != null && os.size() != 0) {
					Class<?> _type = os.get(0).getClass();
					if (BeanUtils.isSimpleProperty(_type) || _type.isAssignableFrom(List.class)
							|| _type.isAssignableFrom(Map.class)) {
						_os = os;
					} else {
						_os = Lists.newArrayList();
						for (Object o : os) {
							Map<String, Object> _o = toCascadeMap(o, nullAble);
							_os.add(_o);
						}
					}
				}
				e.setValue(_os);
			}
		});
		return values;
	}

	/**
	 * Bean to Map
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object bean, boolean nullAble) {
		Map<String, Object> map = Maps.newHashMap();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			Set<String> keys = beanMap.keySet();
			for (Object key : keys) {
				Object value = beanMap.get(key);
				if (value != null) {
					if (!nullAble && (value instanceof String && StringUtils.isBlank((String) value))) {
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
	@SuppressWarnings("unchecked")
	public static <T> T toBean(Map<String, Object> data, Class<T> clazz) {
		try {
			Object bean = clazz.newInstance();
			BeanMap beanMap = BeanMap.create(bean);
			beanMap.putAll(handleData(data, clazz));
			return (T) bean;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 处理数据
	 * 
	 * @param data
	 * @return
	 */
	private static <T> Map<String, Object> handleData(Map<String, Object> data, Class<T> clazz) {
		ClassMeta meta = FieldCache.set(clazz);
		meta.getFields().entrySet().forEach(entry -> {
			String name = entry.getKey();
			FieldMeta field = entry.getValue();
			Class<?> fieldClass = field.getFieldClass();
			if (fieldClass.isEnum()) {
				Object value = data.get(name);
				if (value != null && value instanceof String) {
					String _value = (String) value;
					data.put(name, getEnumObject(_value, fieldClass));
				}
			}
		});
		return data;
	}

	/**
	 * 获得枚举
	 * 
	 * @param <T>
	 * @param value
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Enum<?>> T getEnumObject(String value, Class<?> clazz) {
		if (!clazz.isEnum()) {
			return null;
		}
		try {
			Enum<?>[] enumConstants = (Enum<?>[]) clazz.getEnumConstants();
			for (Enum<?> ec : enumConstants) {
				if (((Enum<?>) ec).name().equals(value)) {
					return (T) ec;
				}
			}
			return null;
		} catch (Exception e) {
		}
		return null;
	}
}
