package com.swak.mvc.method;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;

/**
 * 方法返回值
 * @author lifeng
 */
public class ReturnValueMethodParameter extends MethodParameter{

	public ReturnValueMethodParameter(Method method) {
		super(method, -1);
	}
}