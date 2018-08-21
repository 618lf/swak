package com.swak.utils;

import java.lang.reflect.Method;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * 解析方法的参数名称
 * @author lifeng
 */
public class ParameterNameResolver {

	private static ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    
	/**
	 * 获取参数
	 * @param method
	 * @return
	 */
	public static String[] resolveParameterName(Method method) {
		return parameterNameDiscoverer.getParameterNames(method);
	}
}
