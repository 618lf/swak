package com.swak.asm;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.annotation.TimeOut;
import com.swak.utils.ReflectUtils;

/**
 * method cache
 * 
 * @author lifeng
 */
public class MethodCache {

	static Map<Method, MethodMeta> CACHES = new ConcurrentHashMap<>();

	/**
	 * 存储一个method
	 * 
	 * @param method
	 */
	public static MethodMeta set(Method method) {
		CACHES.putIfAbsent(method, new MethodMeta(method));
		return CACHES.get(method);
	}

	/**
	 * 获取method 对应的元数据
	 * 
	 * @param method
	 * @return
	 */
	public static MethodMeta get(Method method) {
		return CACHES.get(method);
	}

	/**
	 * 这里的返回类型应该获取范型的类型
	 * 
	 * @author lifeng
	 */
	public static class MethodMeta {

		private final String methodName;
		private final String methodDesc;
		private final Class<?> returnType;
		private final Class<?>[] parameterTypes;
		private final Class<?> nestedReturnType;
		private final int timeOut;

		public MethodMeta(Method method) {
			this.methodDesc = ReflectUtils.getMethodDesc(method);
			this.methodName = method.getName();
			this.returnType = method.getReturnType();
			this.nestedReturnType = initNestedReturnType(method);
			this.parameterTypes = method.getParameterTypes();
			TimeOut timeOut = method.getAnnotation(TimeOut.class);
			this.timeOut = timeOut != null ? timeOut.value() : -1;
		}

		private Class<?> initNestedReturnType(Method method) {
			Type type = method.getGenericReturnType();
			if (type instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) type;
				return ReflectUtils.getClass(pType.getActualTypeArguments()[0]);
			}
			return returnType;
		}

		public String getMethodName() {
			return methodName;
		}

		public String getMethodDesc() {
			return methodDesc;
		}

		public Class<?> getReturnType() {
			return returnType;
		}

		public Class<?>[] getParameterTypes() {
			return parameterTypes;
		}

		public int getTimeOut() {
			return timeOut;
		}

		public Class<?> getNestedReturnType() {
			return nestedReturnType;
		}

		@Override
		public int hashCode() {
			return this.methodDesc.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof MethodMeta) {
				MethodMeta ident = (MethodMeta) o;
				return this.methodDesc.equals(ident.methodDesc);
			}
			return false;
		}
	}
}