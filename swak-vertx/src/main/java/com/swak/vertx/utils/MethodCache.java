package com.swak.vertx.utils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.utils.ReflectUtils;
import com.swak.vertx.annotation.TimeOut;

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
		private final int timeOut;

		private MethodMeta(Method method) {
			this.methodDesc = ReflectUtils.getMethodDesc(method);
			this.methodName = method.getName();
			this.returnType = method.getReturnType();
			this.parameterTypes = method.getParameterTypes();
			TimeOut timeOut = method.getAnnotation(TimeOut.class);
			this.timeOut = timeOut != null ? timeOut.value() : -1;
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
	}
}