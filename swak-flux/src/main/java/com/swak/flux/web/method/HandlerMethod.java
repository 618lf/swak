package com.swak.flux.web.method;

import java.lang.reflect.Method;

import org.springframework.util.ClassUtils;

import com.swak.flux.web.Handler;
import com.swak.flux.web.annotation.Auth;

/**
 * 也是一个执行链，没有拦截器； 可以将 handler 定义默认的前置执行器
 * 
 * @author lifeng
 */
public class HandlerMethod implements Handler {

	private final Object bean;
	private final Method method;
	private final Class<?> beanType;
	private final MethodParameter[] parameters;
	private final Auth auth;

	/**
	 * Create an instance from a bean instance and a method.
	 */
	public HandlerMethod(Object bean, Method method) {
		this.bean = bean;
		this.beanType = ClassUtils.getUserClass(bean);
		this.method = method;
		this.parameters = initMethodParameters();
		this.auth = this.method.getAnnotation(Auth.class);
	}

	private MethodParameter[] initMethodParameters() {
		int count = this.method.getParameterTypes().length;
		MethodParameter[] result = new MethodParameter[count];
		for (int i = 0; i < count; i++) {
			result[i] = new MethodParameter(this.beanType, this.method, i);
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

	public Auth getAuth() {
		return auth;
	}

	/**
	 * 如果出错了，不能将 Invoke 对象转为 mono.error。
	 * 
	 * @param args
	 * @return
	 */
	public Object doInvoke(Object[] args) {
		try {
			return this.getMethod().invoke(this.getBean(), args);
		} catch (Exception e) {
			throw new RuntimeException("Handler Method Execute Error!", e);
		}
	}

	@Override
	public String toString() {
		return this.method.toGenericString();
	}
}
