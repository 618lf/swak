package com.swak.reactivex.web.method;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;

public class HandlerMethodParameter extends MethodParameter {

	private volatile String parameterName;
	
	public HandlerMethodParameter(Method method, int index) {
		super(method, index);
	}
	
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public String getParameterName() {
		return this.parameterName;
	}
}