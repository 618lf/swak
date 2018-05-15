package com.swak.reactivex.web.method;

import java.lang.reflect.Method;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;

import com.swak.reactivex.web.Handler;

import reactor.core.publisher.Mono;

/**
 * 也是一个执行链，没有拦截器；
 * 可以将 handler 定义默认的前置执行器
 * @author lifeng
 */
public class HandlerMethod implements Handler {

	private final Object bean;
	private final Method method;
	private final Class<?> beanType;
	private final MethodParameter[] parameters;

	/**
	 * Create an instance from a bean instance and a method.
	 */
	public HandlerMethod(Object bean, Method method) {
		this.bean = bean;
		this.beanType = ClassUtils.getUserClass(bean);
		this.method = method;
		this.parameters = initMethodParameters();
	}

	private MethodParameter[] initMethodParameters() {
		int count = this.method.getParameterTypes().length;
		String[] pnames = ParameterNameResolver.resolveParameterName(method);
		MethodParameter[] result = new MethodParameter[count];
		for (int i = 0; i < count; i++) {
			HandlerMethodParameter parameter = new HandlerMethodParameter(this.method, i);
			GenericTypeResolver.resolveParameterType(parameter, this.beanType);
			parameter.setParameterName(pnames[i]);
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
	public Object doInvoke(Object[] args){
		try {
			return this.getMethod().invoke(this.getBean(), args);
		} catch (Exception e) {
			return Mono.error(e);
		}
	}
}
