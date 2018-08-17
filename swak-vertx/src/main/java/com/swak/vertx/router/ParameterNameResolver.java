package com.swak.vertx.router;

import java.lang.reflect.Method;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

public class ParameterNameResolver {

	public static ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	/**
	 * 获取参数
	 * 
	 * @param method
	 * @return
	 */
	public static String[] resolveParameterName(Method method) {
		return parameterNameDiscoverer.getParameterNames(method);
	}
}
