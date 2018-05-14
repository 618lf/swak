package com.swak.actuator.endpoint.web;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

import com.swak.actuator.endpoint.InvocationContext;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.web.annotation.RequestMethod;
import com.swak.reactivex.web.method.AbstractRequestMappingHandlerMapping;
import com.swak.reactivex.web.method.RequestMappingInfo;

/**
 * 将edpoint 映射为 url 地址
 * @author lifeng
 */
public class WebEndpointHandlerMapping extends AbstractRequestMappingHandlerMapping implements Ordered {

	final Collection<ExposableWebEndpoint> endpoints;
	
	public WebEndpointHandlerMapping(Collection<ExposableWebEndpoint> endpoints) {
		this.endpoints = endpoints;
	}
	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	protected void initRequestMappings(ApplicationContext applicationContext) {
		for (ExposableWebEndpoint endpoint : this.endpoints) {
			for (WebOperation operation : endpoint.getOperations()) {
				registerMappingForOperation(endpoint, operation);
			}
		}
	}
	
	private void registerMappingForOperation(ExposableWebEndpoint endpoint,
			WebOperation operation) {
		this.registryMapping(new WebMvcOperationAdapter(endpoint, operation));
	}

	@Override
	protected RequestMappingInfo getMappingForMethod(Object handler, Method method, Class<?> handlerType) {
		if ("handle".equals(method.getName())) {
			WebMvcOperationAdapter operationAdapter = (WebMvcOperationAdapter)handler;
			return RequestMappingInfo.paths(RequestMethod.ALL, operationAdapter.getPath());
		}
		return null;
	}
	
	// ----------- 将endpoint 转为 controller ------------
	@FunctionalInterface
	public interface WebMvcOperation {
		Object handle(HttpServerRequest request);
	}
	
	public class WebMvcOperationAdapter implements WebMvcOperation {
		
		private final ExposableWebEndpoint endpoint;
		private final WebOperation operation;
		
		public WebMvcOperationAdapter(ExposableWebEndpoint endpoint, WebOperation operation) {
			this.endpoint = endpoint;
			this.operation = operation;
		}
		
		@Override
		public Object handle(HttpServerRequest request) {
			Map<String, Object> arguments = getArguments(request);
			return this.operation.invoke(new InvocationContext(arguments));
		}
		
		private Map<String, Object> getArguments(HttpServerRequest request) {
			Map<String, Object> arguments = new LinkedHashMap<>();
			request.getParameterMap().forEach((name, values) -> arguments.put(name,
					values.size() != 1 ? Arrays.asList(values) : values.get(0)));
			request.getPathVariables().forEach((name, value) -> arguments.put(name,value));
			return arguments;
		}
		
		public String getPath() {
			return endpoint.getRootPath() + "/" + operation.getId();
		}
	}
}