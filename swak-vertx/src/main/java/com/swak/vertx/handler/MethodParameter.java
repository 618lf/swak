package com.swak.vertx.handler;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.swak.utils.StringUtils;
import com.swak.vertx.utils.RouterUtils;

/**
 * 参考 org.springframework.core.MethodParameter 不需要所有的情况都支持
 * 
 * @author lifeng
 */
public class MethodParameter {

	private Class<?> clazz;
	private Method method;
	private int parameterIndex;
	private volatile String parameterName;
	private volatile Class<?> parameterType;
	private volatile Type genericParameterType;
	private volatile Class<?> nestedParameterType;
	private volatile Type nestedGenericParameterType;

	public MethodParameter(Class<?> clazz, Method method, int parameterIndex) {
		this.clazz = clazz;
		this.method = method;
		this.parameterIndex = parameterIndex;
		String[] parameterNames = RouterUtils.getParameterNameDiscoverer().getParameterNames(method);
		if (parameterNames != null) {
			this.parameterName = parameterNames[parameterIndex];
		}
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	public void setParameterIndex(int parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	public String getParameterName() {
		if (parameterName == null) {
			String[] parameterNames = RouterUtils.getParameterNameDiscoverer().getParameterNames(method);
			if (parameterNames != null) {
				this.parameterName = parameterNames[parameterIndex];
			} else {
				this.parameterName = StringUtils.EMPTY;
			}
		}
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public Class<?> getParameterType() {
		if (parameterType == null) {
			if (this.parameterIndex < 0) {
				Method method = getMethod();
				parameterType = (method != null ? method.getReturnType() : void.class);
			} else {
				parameterType = this.method.getParameterTypes()[this.parameterIndex];
			}
		}
		return parameterType;
	}

	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}

	public Type getGenericParameterType() {
		if (this.genericParameterType == null) {
			if (this.parameterIndex < 0) {
				Method method = getMethod();
				genericParameterType = (method != null ? method.getGenericReturnType() : void.class);
			} else {
				Type[] genericParameterTypes = this.method.getGenericParameterTypes();
				int index = this.parameterIndex;
				genericParameterType = (index >= 0 && index < genericParameterTypes.length
						? genericParameterTypes[index]
						: getParameterType());
			}
		}
		return genericParameterType;
	}

	public void setGenericParameterType(Type genericParameterType) {
		this.genericParameterType = genericParameterType;
	}

	public Class<?> getNestedParameterType() {
		if (nestedParameterType == null) {
			this.initNestedParameter();
		}
		return nestedParameterType;
	}

	public void setNestedParameterType(Class<?> nestedParameterType) {
		this.nestedParameterType = nestedParameterType;
	}

	public Type getNestedGenericParameterType() {
		if (nestedGenericParameterType == null) {
			this.initNestedParameter();
		}
		return nestedGenericParameterType;
	}

	public void setNestedGenericParameterType(Type nestedGenericParameterType) {
		this.nestedGenericParameterType = nestedGenericParameterType;
	}

	private void initNestedParameter() {
		Type fieldType = this.getGenericParameterType();
		Class<?> fieldClass = this.getParameterType();
		if (fieldType instanceof ParameterizedType) {
			Type[] args = ((ParameterizedType) fieldType).getActualTypeArguments();
			fieldType = args[0];
		}
		if (fieldType instanceof Class) {
			fieldClass = (Class<?>) fieldType;
		} else if (fieldType instanceof ParameterizedType) {
			Type arg = ((ParameterizedType) fieldType).getRawType();
			if (arg instanceof Class) {
				fieldClass = (Class<?>) arg;
			}
		} else {
			fieldClass = Object.class;
		}
		this.nestedGenericParameterType = fieldType;
		this.nestedParameterType = fieldClass;
	}
}