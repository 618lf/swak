package com.swak.vertx.handler;

import java.lang.annotation.Annotation;
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
	private final Class<?> beanType;
	private final String name;
	private final Method method;
	private final MethodParameter[] parameters;
	private volatile Annotation[] annotations;
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

	public Annotation[] getAnnotations() {
		Annotation[] paramAnns = this.annotations;
		if (paramAnns == null) {
			this.annotations = this.method.getAnnotations();
		}
		return paramAnns;
	}

	public <A extends Annotation> Annotation getAnnotation(Class<A> annotationType) {
		Annotation[] paramAnns = this.getAnnotations();
		if (paramAnns != null) {
			for (Annotation ann : paramAnns) {
				if (annotationType.isInstance(ann)) {
					return ann;
				}
			}
			return null;
		}
		return null;
	}

	public <A extends Annotation> boolean hasAnnotation(Class<A> annotationType) {
		Annotation[] paramAnns = this.getAnnotations();
		if (paramAnns != null) {
			for (Annotation ann : paramAnns) {
				if (annotationType.isInstance(ann)) {
					return true;
				}
			}
			return false;
		}
		return false;
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
	 * 调用
	 * 
	 * @param args
	 * @return
	 */
	public Object doInvoke(Object[] args) throws Exception {
		return this.getMethod().invoke(this.getBean(), args);
	}
}