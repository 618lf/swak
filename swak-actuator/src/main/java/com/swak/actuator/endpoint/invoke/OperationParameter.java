package com.swak.actuator.endpoint.invoke;

import java.lang.reflect.Parameter;

/**
 * method 参数
 * @author lifeng
 */
public class OperationParameter {

	private final String name;
	private final Parameter parameter;
	
	OperationParameter(String name, Parameter parameter) {
		this.name = name;
		this.parameter = parameter;
	}
	
	public String getName() {
		return this.name;
	}

	public Class<?> getType() {
		return this.parameter.getType();
	}
}