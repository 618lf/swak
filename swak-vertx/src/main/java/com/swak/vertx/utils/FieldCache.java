package com.swak.vertx.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.utils.Lists;

/**
 * field cache
 * 
 * @author lifeng
 */
public class FieldCache {

	static Map<Class<?>, FieldMeta> CACHES = new ConcurrentHashMap<>();
	
	/**
	 * 得到field
	 * 
	 * @param method
	 */
	public static FieldMeta set(Class<?> type) {
		CACHES.putIfAbsent(type, new FieldMeta(type));
		return CACHES.get(type);
	}
	
	/**
	 * 获取
	 * @param type
	 * @return
	 */
	public static FieldMeta get(Class<?> type) {
		return CACHES.get(type);
	}

	public static class FieldMeta {
		private final Field[] declares;
		private final Field[] supers;

		public FieldMeta(Class<?> type) {
			this.declares = type.getDeclaredFields();
			List<Field> fields = Lists.newArrayList();
			Class<?> cls = type;
			for (; !cls.equals(Object.class); cls = cls.getSuperclass()) {
				Field[] _fields = cls.getDeclaredFields();
				for (Field f : _fields) {
					fields.add(f);
				}
			}
			Field[] _supers = new Field[fields.size()];
			supers = fields.toArray(_supers);
		}

		public Field[] getDeclares() {
			return declares;
		}

		public Field[] getSupers() {
			return supers;
		}
	}
}