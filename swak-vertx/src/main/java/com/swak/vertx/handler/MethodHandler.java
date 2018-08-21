package com.swak.vertx.handler;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;

import com.swak.exception.BaseRuntimeException;
import com.swak.vertx.utils.RouterUtils;

/**
 * 基于 method 的执行器
 * 
 * @author lifeng
 */
public class MethodHandler{

	private final Object bean;
	private final Method method;
	private final Class<?> beanType;
	private final MethodParameter[] parameters;

	/**
	 * Create an instance from a bean instance and a method.
	 */
	public MethodHandler(Object bean, Method method) {
		this.bean = bean;
		this.beanType = ClassUtils.getUserClass(bean);
		this.method = method;
		this.parameters = initMethodParameters();
	}

	private MethodParameter[] initMethodParameters() {
		int count = this.method.getParameterTypes().length;
		MethodParameter[] result = new MethodParameter[count];
		for (int i = 0; i < count; i++) {
			MethodParameter parameter = new MethodParameter(this.method, i);
			parameter.initParameterNameDiscovery(RouterUtils.getParameterNameDiscoverer());
			result[i] = parameter;
		}
		return result;
	}

	public Object getBean() {
		return bean;
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getBeanType() {
		return beanType;
	}

	public MethodParameter[] getParameters() {
		return parameters;
	}

	/**
	 * 如果出错了，则输出 Mono.error(e) 对象
	 * 
	 * @param args
	 * @return
	 */
	public Object doInvoke(Object[] args) {
		try {
			return this.getMethod().invoke(this.getBean(), args);
		} catch (Exception e) {
			throw new BaseRuntimeException("invoke method error");
		}
	}
}