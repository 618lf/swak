package com.swak.vertx.handler;

import java.lang.reflect.Method;

import org.springframework.util.ClassUtils;

import com.swak.meters.MethodMetrics;
import com.swak.meters.MetricsFactory;
import com.swak.utils.ReflectUtils;

/**
 * 基于 method 的执行器
 * 
 * @author lifeng
 */
@SuppressWarnings("rawtypes")
public class MethodHandler {

	private final Object bean;
	private final Method method;
	private final Class<?> beanType;
	private final MethodParameter[] parameters;
	private final String name;
	protected MethodMetrics metrics;

	/**
	 * Create an instance from a bean instance and a method.
	 */
	public MethodHandler(Object bean, Method method) {
		this.bean = bean;
		this.beanType = ClassUtils.getUserClass(bean);
		this.method = method;
		this.name = new StringBuilder(this.beanType.getName()).append(".")
				.append(ReflectUtils.getMethodDesc(this.method)).toString();
		this.parameters = initMethodParameters();
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

	/**
	 * 设置监控
	 * 
	 * @param metricsFactory
	 * @return
	 */
	public MethodHandler applyMetrics(MetricsFactory metricsFactory) {
		if (metricsFactory != null) {
			metrics = metricsFactory.createMethodMetrics(this.method, name);
		}
		return this;
	}

	/**
	 * 如果出错了，则输出 Mono.error(e) 对象
	 * 
	 * @param args
	 * @return
	 */
	public Object doInvoke(Object[] args) throws Exception {
		return this.getMethod().invoke(this.getBean(), args);
	}
}