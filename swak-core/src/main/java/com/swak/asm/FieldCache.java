package com.swak.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.utils.Maps;
import com.swak.utils.ReflectUtils;

/**
 * field cache
 * 
 * @author lifeng
 */
public class FieldCache {

	static Map<Class<?>, ClassMeta> CACHES = new ConcurrentHashMap<>();

	/**
	 * 得到field
	 * 
	 * @param method
	 */
	public static ClassMeta set(Class<?> type) {
		CACHES.putIfAbsent(type, new ClassMeta(type));
		return CACHES.get(type);
	}

	/**
	 * 获取
	 * 
	 * @param type
	 * @return
	 */
	public static ClassMeta get(Class<?> type) {
		return CACHES.get(type);
	}

	/**
	 * 类型元数据
	 * 
	 * @author lifeng
	 */
	public static class ClassMeta {

		// 用属性名称作为key
		private final Map<String, FieldMeta> fields;

		public ClassMeta(Class<?> type) {

			// 所有的字段
			fields = Maps.newHashMap();

			// 声明的字段
			Field[] declaredFields = type.getDeclaredFields();

			// 所有的方法，包括父类的
			Method[] methods = type.getMethods();
			for (Method method : methods) {

				String methodName = method.getName();
				if (!methodName.startsWith("set") || Modifier.isStatic(method.getModifiers())) {
					continue;
				}

				// support builder set
				Class<?> returnType = method.getReturnType();
				if (!(returnType.equals(Void.TYPE) || returnType.equals(method.getDeclaringClass()))) {
					continue;
				}

				// exclude object method
				if (method.getDeclaringClass() == Object.class) {
					continue;
				}

				// only set
				Class<?>[] types = method.getParameterTypes();
				if (types.length != 1) {
					continue;
				}

				char c3 = methodName.charAt(3);
				String propertyName;
				if (Character.isUpperCase(c3) //
						|| c3 > 512 // for unicode method name
				) {
					propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
				} else if (c3 == '_') {
					propertyName = methodName.substring(4);
				} else if (c3 == 'f') {
					propertyName = methodName.substring(3);
				} else if (methodName.length() >= 5 && Character.isUpperCase(methodName.charAt(4))) {
					propertyName = ReflectUtils.propertyName(methodName.substring(3));
				} else {
					continue;
				}

				// 获得字段，考虑到如果是 boolean字段时的情形
				Field field = ReflectUtils.getField(type, propertyName, declaredFields);
				if (field == null && types[0] == boolean.class) {
					String isFieldName = "is" + Character.toUpperCase(propertyName.charAt(0))
							+ propertyName.substring(1);
					field = ReflectUtils.getField(type, isFieldName, declaredFields);
				}

				// 设置可以访问
				method.setAccessible(true);
				field.setAccessible(true);
				this.addField(new FieldMeta(type, propertyName, method, field));
			}
		}

		private void addField(FieldMeta meta) {
			fields.put(meta.getPropertyName(), meta);
		}

		public Map<String, FieldMeta> getFields() {
			return fields;
		}
	}

	/**
	 * 字段元数据
	 * 
	 * @author lifeng
	 */
	public static class FieldMeta {
		private final String propertyName;
		private final Method method;
		private final Field field;
		private Class<?> fieldClass;
		private Type fieldType;

		public FieldMeta(Class<?> clazz, String propertyName, Method method, Field field) {
			this.propertyName = propertyName;
			this.method = method;
			this.field = field;

			// 只有一个参数
			Type fieldType = method.getGenericParameterTypes()[0];
			Class<?> fieldClass = method.getParameterTypes()[0];

			if (clazz != null && fieldClass == Object.class && fieldType instanceof TypeVariable) {
				TypeVariable<?> tv = (TypeVariable<?>) fieldType;
				Type genericFieldType = ReflectUtils.getInheritGenericType(clazz, tv);
				if (genericFieldType != null) {
					this.fieldClass = ReflectUtils.getClass(genericFieldType);
					this.fieldType = genericFieldType;
					return;
				}
			}

			Type genericFieldType = fieldType;
			if (!(fieldType instanceof Class)) {
				genericFieldType = ReflectUtils.getFieldType(clazz, fieldType);
				if (genericFieldType != fieldType) {
					if (genericFieldType instanceof ParameterizedType) {
						fieldClass = ReflectUtils.getClass(genericFieldType);
					} else if (genericFieldType instanceof Class) {
						fieldClass = ReflectUtils.getClass(genericFieldType);
					}
				}
			}

			this.fieldType = genericFieldType;
			this.fieldClass = fieldClass;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public Method getMethod() {
			return method;
		}

		public Field getField() {
			return field;
		}

		public Class<?> getFieldClass() {
			return fieldClass;
		}

		public Type getFieldType() {
			return fieldType;
		}
	}
}