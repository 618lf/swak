package com.swak.asm;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.Constants;
import com.swak.annotation.FluxService;
import com.swak.annotation.RestApi;
import com.swak.annotation.RestPage;
import com.swak.annotation.TimeOut;
import com.swak.exception.BaseRuntimeException;
import com.swak.utils.Maps;
import com.swak.utils.Sets;

/**
 * 方法元数据
 * 
 * @author lifeng
 * @date 2020年4月20日 下午6:10:43
 */
public class MethodCache {

	/**
	 * 日志
	 */
	static Logger logger = LoggerFactory.getLogger(MethodCache.class);

	/**
	 * 缓存
	 */
	static Map<Class<?>, ClassMeta> CACHES = new ConcurrentHashMap<>();

	/**
	 * 设置 field
	 *
	 * @param type 字段类型
	 * @return ClassMeta 类型元数据
	 * @author lifeng
	 * @date 2020/3/28 17:33
	 */
	public static ClassMeta set(Class<?> type) {
		CACHES.computeIfAbsent(type, (key) -> {
			return new ClassMeta(type);
		});
		return CACHES.get(type);
	}

	/**
	 * 获取元数据
	 *
	 * @param type 类型
	 * @return ClassMeta 类型元数据
	 * @author lifeng
	 * @date 2020/3/28 17:35
	 */
	public static ClassMeta get(Class<?> type) {
		return CACHES.get(type);
	}

	/**
	 * 类型元数据
	 *
	 * @author: lifeng
	 * @date: 2020/3/28 17:36
	 */
	public static class ClassMeta {

		/**
		 * 创建index
		 */
		private Map<String, MethodMeta> namedIndex;
		private Map<Method, MethodMeta> methodIndex;

		/**
		 * 缓存整个类型的 public方法
		 * 
		 * @param type 类型和接口
		 * @return 所有的方法元
		 */
		ClassMeta(Class<?> type) {

			// 所有的元数据
			Set<MethodMeta> metas = Sets.newHashSet();

			// 级联处理元数据
			this.cascadeBuildIn(metas, type, null);

			// 如果是接口或者Api则会通过 Method找 Meta
			if (type.isInterface() || type.getAnnotation(RestApi.class) != null
					|| type.getAnnotation(RestPage.class) != null) {
				this.methodIndex = Maps.newHashMap();
				for (MethodMeta meta : metas) {
					this.methodIndex.put(meta.getMethod(), meta);
				}
			}
			// 如果仅仅是服务
			else if (type.getAnnotation(FluxService.class) != null) {
				this.namedIndex = Maps.newHashMap();
				for (MethodMeta meta : metas) {
					this.namedIndex.put(meta.getMethodDesc(), meta);
				}
			}
			// 服务并Api或者其他的类
			else {
				this.methodIndex = Maps.newHashMap();
				this.namedIndex = Maps.newHashMap();
				for (MethodMeta meta : metas) {
					this.methodIndex.put(meta.getMethod(), meta);
					this.namedIndex.put(meta.getMethodDesc(), meta);
				}
			}

			// for gc
			metas.clear();
			metas = null;
		}

		/**
		 * 缓存整个类型的 public方法
		 * 
		 * @param type 类型和接口
		 * @return 所有的方法元
		 */
		private void cascadeBuildIn(Set<MethodMeta> metas, Class<?> type,
				Map<TypeVariable<?>, Type> paramVariablesMappers) {

			// 只处理自己声明的方法
			buildIn(metas, type, paramVariablesMappers);

			// 父类
			Type[] extendsTypes = null;
			if (type.isInterface()) {
				extendsTypes = type.getGenericInterfaces();
			} else if (type.getGenericSuperclass() != null && type.getSuperclass() != Object.class) {
				extendsTypes = new Type[] { type.getGenericSuperclass() };
			}

			if (extendsTypes != null) {
				for (Type iType : extendsTypes) {

					// 普通类型
					if (iType instanceof Class) {
						buildIn(metas, (Class<?>) iType, paramVariablesMappers);
					}

					// 泛型类型
					else if (iType instanceof ParameterizedType
							&& ((ParameterizedType) iType).getRawType() instanceof Class) {

						// 类型映射转换
						ParameterizedType parameterizedType = (ParameterizedType) iType;
						Map<TypeVariable<?>, Type> actualMappers = paramVariablesMappers(
								parameterizedType.getActualTypeArguments(),
								((Class<?>) parameterizedType.getRawType()).getTypeParameters(), paramVariablesMappers);

						// 处理父类型
						cascadeBuildIn(metas, (Class<?>) parameterizedType.getRawType(), actualMappers);
					}

					// 其他不支持
					else {
						throw new BaseRuntimeException("Supper Class Type Not Support " + iType);
					}
				}
			}
		}

		/**
		 * 创建方法,保证不覆盖子类中的复写方法
		 */
		private void buildIn(Set<MethodMeta> metas, Class<?> type, Map<TypeVariable<?>, Type> paramVariablesMappers) {

			// 当前类声明的类型
			Method[] methods = type.getDeclaredMethods();

			// 简单方法的缓存
			for (Method method : methods) {
				if (!method.isBridge() && (method.getModifiers() & Modifier.PUBLIC) > 0) {
					MethodMeta meta = new MethodMeta(method, paramVariablesMappers);
					if (!metas.contains(meta)) {
						metas.add(meta);
					}
				}
			}
		}

		/**
		 * 类型变量和实际类型映射
		 */
		private Map<TypeVariable<?>, Type> paramVariablesMappers(Type[] actualTypes, TypeVariable<?>[] typeParameters,
				Map<TypeVariable<?>, Type> actualMappers) {

			// 类型变量和实际类型映射
			Map<TypeVariable<?>, Type> paramVariablesMappers = Maps.newHashMap();
			for (int i = 0; i < typeParameters.length; i++) {

				// 实际的类型
				Type actualType = actualTypes[i];

				// 如果实际类型直接是类类型
				if (actualType instanceof Class) {
					actualType = actualTypes[i];
				}
				// 如果实际类型是 参数类型
				else if (actualType instanceof ParameterizedType || actualType instanceof TypeVariable) {
					actualType = getActualParameterizedType(actualType, actualMappers);
				}
				// 不支持的格式
				else {
					throw new BaseRuntimeException("Type Parameter Not Support " + actualType);
				}
				paramVariablesMappers.put(typeParameters[i], actualType);
			}
			return paramVariablesMappers;
		}

		// 封装之后的类型
		private Type getActualParameterizedType(Type type, Map<TypeVariable<?>, Type> actualMappers) {
			if (actualMappers == null) {
				return type;
			}
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Type[] aTypes = parameterizedType.getActualTypeArguments();
				Type[] nestTypes = new Type[aTypes.length];
				for (int i = 0; i < aTypes.length; i++) {
					Type aType = aTypes[i];
					if (aType instanceof Class) {
						nestTypes[i] = aType;
					} else if (aType instanceof TypeVariable) {
						nestTypes[i] = actualMappers.get(aType);
					} else {
						throw new BaseRuntimeException("Actual Type Arguments Not Support " + aType);
					}
				}
				return new ParameterizedTypeHolder(nestTypes, parameterizedType.getOwnerType(),
						parameterizedType.getRawType());
			} else if (type instanceof TypeVariable) {
				return actualMappers.get(type);
			}
			throw new BaseRuntimeException("not support");
		}

		/**
		 * 返回缓存的方法元数据
		 * 
		 * @return
		 */
		public Collection<MethodMeta> getMethods() {
			return namedIndex != null ? namedIndex.values() : methodIndex.values();
		}

		/**
		 * 通过方法签名查找
		 * 
		 * @param signature 方法签名
		 * @return MethodMeta
		 */
		public MethodMeta lookup(String named) {
			if (!namedIndex.containsKey(named)) {
				throw new BaseRuntimeException("Please Set Method Public.");
			}
			return namedIndex.get(named);
		}

		/**
		 * 通过方法查找
		 * 
		 * @param method 方法
		 * @return MethodMeta
		 */
		public MethodMeta lookup(Method method) {
			if (!methodIndex.containsKey(method)) {
				throw new BaseRuntimeException("Please Set method Public.");
			}
			return methodIndex.get(method);
		}
	}

	/**
	 * 这里的返回类型应该获取范型的类型
	 *
	 * @author lifeng
	 */
	public static class MethodMeta {

		public static final String PARAM_CLASS_SPLIT = ",";
		public static final String EMPTY_PARAM = "void";

		private final Method method;
		private final String methodName;
		private final String methodDesc;
		private final Class<?> returnType;
		private final Class<?> nestedReturnType;
		private final Class<?>[] parameterTypes;
		private final Class<?>[] nestedParameterTypes;
		private final int timeOut;

		/**
		 * 操作符： 定义类的一些特殊属性
		 */
		protected byte operators = 0;

		/**
		 * 无泛型的方法
		 */
		public MethodMeta(Method method) {
			this(method, null);
		}

		/**
		 * 泛型接口中的方法
		 */
		public MethodMeta(Method method, Map<TypeVariable<?>, Type> paramVariablesMappers) {
			this.method = method;
			this.methodName = method.getName();
			this.returnType = this.getReturnType(paramVariablesMappers);
			this.parameterTypes = this.getParameterTypes(paramVariablesMappers);
			this.nestedReturnType = this.initNestedReturnType(paramVariablesMappers);
			this.nestedParameterTypes = this.initNestedParameterTypes(paramVariablesMappers);
			this.methodDesc = this.buildMethodDesc();
			TimeOut timeOut = method.getAnnotation(TimeOut.class);
			this.timeOut = timeOut != null ? timeOut.value() : -1;
			this.initOperators(method);
		}

		/**
		 * 获得返回值 -- 直接类型
		 * 
		 * @param paramVariablesMappers 泛型类型定义
		 * @return 返回值
		 */
		private Class<?> getReturnType(Map<TypeVariable<?>, Type> paramVariablesMappers) {
			Type type = method.getGenericReturnType();
			if (paramVariablesMappers != null && type instanceof TypeVariable) {
				return this.getActualType((TypeVariable<?>) type, paramVariablesMappers);
			}
			return method.getReturnType();
		}

		/**
		 * 参数类型 -- 直接类型
		 * 
		 * @param paramVariablesMappers 泛型类型定义
		 * @return
		 */
		private Class<?>[] getParameterTypes(Map<TypeVariable<?>, Type> paramVariablesMappers) {
			Class<?>[] actualTypes = method.getParameterTypes();
			Type[] parameterTypes = method.getGenericParameterTypes();
			if (paramVariablesMappers != null && actualTypes != null && actualTypes.length > 0 && parameterTypes != null
					&& parameterTypes.length > 0) {
				actualTypes = this.shallowCopy(actualTypes);
				for (int i = 0; i < parameterTypes.length; i++) {
					if (parameterTypes[i] instanceof TypeVariable) {
						actualTypes[i] = this.getActualType((TypeVariable<?>) parameterTypes[i], paramVariablesMappers);
					}
				}
			}
			return actualTypes;
		}

		// 浅拷贝
		private Class<?>[] shallowCopy(Class<?>[] source) {
			Class<?>[] actualTypes = new Class<?>[source.length];
			for (int i = 0; i < source.length; i++) {
				actualTypes[i] = source[i];
			}
			return actualTypes;
		}

		/**
		 * 内部实际的类型 -- 不需要支持多级泛型
		 */
		private Class<?> initNestedReturnType(Map<TypeVariable<?>, Type> paramVariablesMappers) {
			Type type = method.getGenericReturnType();
			if (type instanceof Class) {
				return returnType;
			} else if (type instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) type;
				Type actualType = pType.getActualTypeArguments()[0];
				if (actualType instanceof Class) {
					return (Class<?>) actualType;
				} else if (paramVariablesMappers != null && actualType instanceof TypeVariable) {
					return this.getNestActualType((TypeVariable<?>) actualType, paramVariablesMappers);
				}
			} else if (paramVariablesMappers != null && type instanceof TypeVariable) {
				return this.getNestActualType((TypeVariable<?>) type, paramVariablesMappers);
			}
			if (logger.isDebugEnabled()) {
				logger.warn("Method【{}】  Might Miss Real Return Type.", this.getMethodDesc());
			}
			return Object.class;
		}

		/**
		 * 内部实际的类型 -- 不需要支持多级泛型
		 */
		private Class<?>[] initNestedParameterTypes(Map<TypeVariable<?>, Type> paramVariablesMappers) {
			Class<?>[] actualTypes = this.parameterTypes;
			Type[] parameterTypes = method.getGenericParameterTypes();
			if (actualTypes != null && actualTypes.length > 0) {
				actualTypes = this.shallowCopy(actualTypes);
				for (int i = 0; i < parameterTypes.length; i++) {
					if (parameterTypes[i] instanceof ParameterizedType) {
						ParameterizedType pType = (ParameterizedType) parameterTypes[i];
						Type actualType = pType.getActualTypeArguments()[0];
						if (actualType instanceof Class) {
							actualTypes[i] = (Class<?>) actualType;
						} else if (paramVariablesMappers != null && actualType instanceof TypeVariable) {
							actualTypes[i] = this.getNestActualType((TypeVariable<?>) actualType,
									paramVariablesMappers);
						} else {
							if (logger.isDebugEnabled()) {
								logger.warn("Method【{}】  Might Miss Real Nested Param Type.", this.getMethodDesc());
							}
							actualTypes[i] = Object.class;
						}
					} else if (parameterTypes[i] instanceof TypeVariable) {
						actualTypes[i] = this.getNestActualType((TypeVariable<?>) parameterTypes[i],
								paramVariablesMappers);
					}
				}
			}
			return actualTypes;
		}

		private Class<?> getActualType(Type type, Map<TypeVariable<?>, Type> paramVariablesMappers) {
			Type actualType = paramVariablesMappers.get(type);
			if (actualType != null && actualType instanceof Class) {
				return (Class<?>) actualType;
			} else if (actualType != null && actualType instanceof ParameterizedType) {
				Type ptype = ((ParameterizedType) actualType).getRawType();
				if (ptype instanceof Class) {
					return (Class<?>) ptype;
				}
			}
			return Object.class;
		}

		private Class<?> getNestActualType(Type type, Map<TypeVariable<?>, Type> paramVariablesMappers) {
			Type actualType = paramVariablesMappers.get(type);
			if (actualType != null && actualType instanceof Class) {
				return (Class<?>) actualType;
			} else if (actualType != null && actualType instanceof ParameterizedType) {
				Type ptype = ((ParameterizedType) actualType).getActualTypeArguments()[0];
				if (ptype instanceof Class) {
					return (Class<?>) ptype;
				} else if (ptype instanceof TypeVariable) {
					return this.getActualType(ptype, paramVariablesMappers);
				}
			}
			return Object.class;
		}

		private void initOperators(Method method) {
			if (method.getDeclaringClass().equals(Object.class)) {
				operators |= Constants.OPERATORS_LOCAL;
			}
			if (Future.class.isAssignableFrom(method.getReturnType())) {
				operators |= Constants.OPERATORS_ASYNC;
			}
		}

		public Method getMethod() {
			return method;
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

		public Class<?>[] getNestedParameterTypes() {
			return nestedParameterTypes;
		}

		public boolean isAsync() {
			return (this.operators & Constants.OPERATORS_ASYNC) == Constants.OPERATORS_ASYNC;
		}

		public boolean isLocal() {
			return (this.operators & Constants.OPERATORS_LOCAL) == Constants.OPERATORS_LOCAL;
		}

		private String buildMethodDesc() {
			String methodParamDesc = EMPTY_PARAM;
			if (!(this.parameterTypes == null || this.parameterTypes.length == 0)) {
				StringBuilder builder = new StringBuilder();
				Class<?>[] clzs = this.parameterTypes;
				for (Class<?> clz : clzs) {
					String className = buildClassName(clz);
					builder.append(className).append(PARAM_CLASS_SPLIT);
				}
				methodParamDesc = builder.substring(0, builder.length() - 1);
			}
			return methodName + "(" + methodParamDesc + ")";
		}

		private String buildClassName(Class<?> c) {
			if (c.isArray()) {
				StringBuilder sb = new StringBuilder();
				do {
					sb.append("[]");
					c = c.getComponentType();
				} while (c.isArray());

				return c.getName() + sb.toString();
			}
			return c.getName();
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

		@Override
		public String toString() {
			return this.methodDesc;
		}
	}

	/**
	 * 封装的ParameterizedType 处理父父类中的泛型参数
	 * 
	 * @author lifeng
	 * @date 2020年4月22日 上午11:21:26
	 */
	static class ParameterizedTypeHolder implements ParameterizedType {
		private final Type[] actualTypeArguments;
		private final Type ownerType;
		private final Type rawType;

		public ParameterizedTypeHolder(Type[] actualTypeArguments, Type ownerType, Type rawType) {
			this.actualTypeArguments = actualTypeArguments;
			this.ownerType = ownerType;
			this.rawType = rawType;
		}

		public Type[] getActualTypeArguments() {
			return actualTypeArguments;
		}

		public Type getOwnerType() {
			return ownerType;
		}

		public Type getRawType() {
			return rawType;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			ParameterizedTypeHolder that = (ParameterizedTypeHolder) o;

			// Probably incorrect - comparing Object[] arrays with Arrays.equals
			if (!Arrays.equals(actualTypeArguments, that.actualTypeArguments))
				return false;
			if (ownerType != null ? !ownerType.equals(that.ownerType) : that.ownerType != null)
				return false;
			return rawType != null ? rawType.equals(that.rawType) : that.rawType == null;

		}

		@Override
		public int hashCode() {
			int result = actualTypeArguments != null ? Arrays.hashCode(actualTypeArguments) : 0;
			result = 31 * result + (ownerType != null ? ownerType.hashCode() : 0);
			result = 31 * result + (rawType != null ? rawType.hashCode() : 0);
			return result;
		}
	}
}