package com.swak.actuator.endpoint;

import java.util.Map;

public class InvocationContext {

	private final Map<String, Object> arguments;
	
	public InvocationContext(Map<String, Object> arguments) {
		this.arguments = arguments;
	}
	
	public Map<String, Object> getArguments() {
		return this.arguments;
	}
}
