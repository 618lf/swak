package com.swak.vertx.utils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.utils.ReflectUtils;

/**
 * method 还存
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

	public static class MethodMeta {
		private final String methodName;
		private final String methodDesc;
		private final Class<?> returnType;
		private final Class<?>[] parameterTypes;

		private MethodMeta(Method method) {
			this.methodDesc = ReflectUtils.getMethodDesc(method);
			this.methodName = method.getName();
			this.returnType = method.getReturnType();
			this.parameterTypes = method.getParameterTypes();
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
	}
}