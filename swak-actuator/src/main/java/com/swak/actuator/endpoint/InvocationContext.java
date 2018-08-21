package com.swak.actuator.endpoint;

import java.util.Map;

/**
 * 调用的上下文
 * 
 * @author lifeng
 */
public class InvocationContext {

	private final Map<String, Object> arguments;
	private final Object request;

	public InvocationContext(Object request, Map<String, Object> arguments) {
		this.request = request;
		this.arguments = arguments;
	}

	public Map<String, Object> getArguments() {
		return this.arguments;
	}
	public Object getRequest() {
		return request;
	}
}