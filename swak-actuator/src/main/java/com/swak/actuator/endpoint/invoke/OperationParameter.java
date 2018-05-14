package com.swak.actuator.endpoint.invoke;

import java.lang.reflect.Parameter;

/**
 * method 参数
 * @author lifeng
 */
public class OperationParameter {

	private final String name;
	private final boolean selector;
	private final Parameter parameter;
	
	OperationParameter(String name, Parameter parameter) {
		this(name, parameter, false);
	}
	
	OperationParameter(String name, Parameter parameter, boolean selector) {
		this.name = name;
		this.parameter = parameter;
		this.selector = selector;
	}
	
	public String getName() {
		return this.name;
	}
	public boolean isSelector() {
		return selector;
	}
	public Class<?> getType() {
		return this.parameter.getType();
	}
}