package com.swak.reliable.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.asm.Wrapper;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 执行器
 * 
 * @author DELL
 */
@Getter
@Setter
@Accessors(chain = true)
public class Invoker {

	private Class<?> fac;
	private Object service;
	private Method method;
	private Wrapper wrapper;
	private MethodMeta methodMeta;

	public Invoker(Class<?> fac, Object service, Method method) {
		this.fac = fac;
		this.service = service;
		this.method = method;
		this.wrapper = Wrapper.getWrapper(fac);
		this.methodMeta = MethodCache.get(fac).lookup(this.method);
	}

	public Object invoke(Object[] params) throws NoSuchMethodException, InvocationTargetException {
		return this.wrapper.invokeMethod(service, this.methodMeta.getMethodDesc(), params);
	}
}
