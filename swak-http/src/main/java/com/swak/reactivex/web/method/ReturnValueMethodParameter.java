package com.swak.reactivex.web.method;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;

import io.reactivex.Observable;

/**
 * 方法返回值
 * @author lifeng
 */
public class ReturnValueMethodParameter extends MethodParameter {

	public ReturnValueMethodParameter(Method method) {
		super(method, -1);
	}
	
	@Override
	public boolean isOptional() {
		return getParameterType() == Observable.class || super.isOptional();
	}
	
	public MethodParameter nestedIfOptional() {
		return (isOptional() ? nested() : this);
	}
}