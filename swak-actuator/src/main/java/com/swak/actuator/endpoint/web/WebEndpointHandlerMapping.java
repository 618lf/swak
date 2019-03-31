package com.swak.actuator.endpoint.web;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.Ordered;

import com.swak.Constants;
import com.swak.actuator.endpoint.InvocationContext;
import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.web.annotation.RequestMethod;
import com.swak.flux.web.method.AbstractRequestMappingHandlerMapping;
import com.swak.flux.web.method.RequestMappingInfo;

/**
 * 将edpoint 映射为 url 地址
 * 
 * web flux 的方式來訪問
 * 
 * @author lifeng
 */
public class WebEndpointHandlerMapping extends AbstractRequestMappingHandlerMapping implements Ordered {

	final WebEndpointsSupplier webEndpointsSupplier;
	final Collection<ExposableWebEndpoint> endpoints;

	public WebEndpointHandlerMapping(WebEndpointsSupplier webEndpointsSupplier) {
		this.webEndpointsSupplier = webEndpointsSupplier;
		this.endpoints = webEndpointsSupplier.getEndpoints();
		this.initRequestMappings();
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	protected void initRequestMappings() {
		for (ExposableWebEndpoint endpoint : this.endpoints) {
			for (WebOperation operation : endpoint.getOperations()) {
				registerMappingForOperation(operation);
			}
		}
	}

	private void registerMappingForOperation(WebOperation operation) {
		this.registryMapping(new WebMvcOperationAdapter(webEndpointsSupplier.getRootPath(), operation));
	}

	@Override
	protected RequestMappingInfo getMappingForMethod(Object handler, Method method, Class<?> handlerType) {
		if ("handle".equals(method.getName())) {
			WebMvcOperationAdapter operationAdapter = (WebMvcOperationAdapter) handler;
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

		private final String rootPath;
		private final WebOperation operation;

		public WebMvcOperationAdapter(String rootPath, WebOperation operation) {
			this.rootPath = rootPath;
			this.operation = operation;
		}

		@Override
		public Object handle(HttpServerRequest request) {
			Map<String, Object> arguments = getArguments(request);
			return this.operation.invoke(new InvocationContext(request, arguments));
		}

		private Map<String, Object> getArguments(HttpServerRequest request) {
			Map<String, Object> arguments = new LinkedHashMap<>();
			request.getParameterMap().forEach(
					(name, values) -> arguments.put(name, values.size() != 1 ? Arrays.asList(values) : values.get(0)));
			request.getPathVariables().forEach((name, value) -> arguments.put(name, value));
			return arguments;
		}

		public String getPath() {
			return new StringBuilder(rootPath).append(Constants.URL_PATH_SEPARATE).append(operation.getId()).toString();
		}

		@Override
		public String toString() {
			return operation.toString();
		}
	}
}