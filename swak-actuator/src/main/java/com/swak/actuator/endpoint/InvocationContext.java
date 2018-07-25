package com.swak.actuator.endpoint;

import java.util.Map;

import com.swak.reactivex.transport.http.server.HttpServerRequest;

/**
 * 调用的上下文
 * 
 * @author lifeng
 */
public class InvocationContext {

	private final Map<String, Object> arguments;
	private final HttpServerRequest request;

	public InvocationContext(HttpServerRequest request, Map<String, Object> arguments) {
		this.request = request;
		this.arguments = arguments;
	}

	public Map<String, Object> getArguments() {
		return this.arguments;
	}
	public HttpServerRequest getRequest() {
		return request;
	}
}