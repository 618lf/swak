package com.swak.asm;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.swak.Constants;
import com.swak.annotation.TimeOut;
import com.swak.utils.ReflectUtils;

/**
 * method cache
 *
 * @author: lifeng
 * @date: 2020/3/28 17:40
 */
public class MethodCache {

	static Map<Method, MethodMeta> CACHES = new ConcurrentHashMap<>();

	/**
	 * 存储一个method
	 *
	 * @param method 方法
	 * @return MethodMeta 方法元数据
	 * @author lifeng
	 * @date 2020/3/28 17:40
	 */
	public static MethodMeta set(Method method) {
		CACHES.putIfAbsent(method, new MethodMeta(method));
		return CACHES.get(method);
	}

	/**
	 * 获得方法的元数据
	 *
	 * @param method 方法
	 * @return MethodMeta 方法元数据
	 * @author lifeng
	 * @date 2020/3/28 17:40
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

		/**
		 * 操作符： 定义类的一些特殊属性
		 */
		protected byte operators = 0;

		public MethodMeta(Method method) {
			this.methodDesc = ReflectUtils.getMethodDesc(method);
			this.methodName = method.getName();
			this.returnType = method.getReturnType();
			this.nestedReturnType = initNestedReturnType(method);
			this.parameterTypes = method.getParameterTypes();
			TimeOut timeOut = method.getAnnotation(TimeOut.class);
			this.timeOut = timeOut != null ? timeOut.value() : -1;
			this.initOperators(method);
		}

		private Class<?> initNestedReturnType(Method method) {
			Type type = method.getGenericReturnType();
			if (type instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) type;
				return ReflectUtils.getClass(pType.getActualTypeArguments()[0]);
			}
			return returnType;
		}

		private void initOperators(Method method) {
			if (method.getDeclaringClass().equals(Object.class)) {
				operators |= Constants.OPERATORS_LOCAL;
			}
			if (Future.class.isAssignableFrom(method.getReturnType())) {
				operators |= Constants.OPERATORS_ASYNC;
			}
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

		public boolean isAsync() {
			return (this.operators & Constants.OPERATORS_ASYNC) == Constants.OPERATORS_ASYNC;
		}

		public boolean isLocal() {
			return (this.operators & Constants.OPERATORS_LOCAL) == Constants.OPERATORS_LOCAL;
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