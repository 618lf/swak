package com.swak.reactivex.web.method;

import java.lang.reflect.Method;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;

import com.swak.reactivex.web.Handler;

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
	 * 执行
	 * 
	 * @param args
	 * @return
	 */
	public Object doInvoke(Object[] args){
		try {
			return this.getMethod().invoke(this.getBean(), args);
		} catch (IllegalAccessException ex) {
			String text = (ex.getMessage() != null ? ex.getMessage() : "Illegal argument");
			throw new IllegalStateException(getInvocationErrorMessage(text, args), ex);
		} catch (Exception e) {
			return null;
		}
	}

	private String getInvocationErrorMessage(String text, Object[] resolvedArgs) {
		StringBuilder sb = new StringBuilder(getDetailedErrorMessage(text));
		sb.append("Resolved arguments: \n");
		for (int i = 0; i < resolvedArgs.length; i++) {
			sb.append("[").append(i).append("] ");
			if (resolvedArgs[i] == null) {
				sb.append("[null] \n");
			} else {
				sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
				sb.append("[value=").append(resolvedArgs[i]).append("]\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Adds HandlerMethod details such as the bean type and method signature to the
	 * message.
	 * 
	 * @param text
	 *            error message to append the HandlerMethod details to
	 */
	private String getDetailedErrorMessage(String text) {
		StringBuilder sb = new StringBuilder(text).append("\n");
		sb.append("HandlerMethod details: \n");
		sb.append("Controller [").append(getBeanType().getName()).append("]\n");
		sb.append("Method [").append(this.getMethod().toGenericString()).append("]\n");
		return sb.toString();
	}
}
