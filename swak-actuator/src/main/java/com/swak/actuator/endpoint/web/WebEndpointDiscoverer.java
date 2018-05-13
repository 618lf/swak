package com.swak.actuator.endpoint.web;

import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.context.ApplicationContext;

import com.swak.actuator.endpoint.annotation.EndpointDiscoverer;
import com.swak.actuator.endpoint.invoke.WebOperationInvoker;

/**
 * 基于web 的 endpoint
 * @author lifeng
 */
public class WebEndpointDiscoverer extends EndpointDiscoverer<ExposableWebEndpoint, WebOperation> implements WebEndpointsSupplier{

    private final String rootPath;
	
	public WebEndpointDiscoverer(String rootPath, ApplicationContext applicationContext) {
		super(applicationContext);
		this.rootPath = rootPath;
	}

	@Override
	protected ExposableWebEndpoint createEndpoint(EndpointBean endpointBean, Collection<WebOperation> operations) {
		return new DiscoveredWebEndpoint(this, endpointBean, rootPath,  operations);
	}

	@Override
	protected WebOperation createOperation(EndpointBean endpointBean, Method method) {
		return new DiscoveredWebOperation(endpointBean, new WebOperationInvoker(endpointBean.getBean(), method));
	}

	@Override
	protected OperationKey createOperationKey(WebOperation operation) {
		return new OperationKey(operation.getId(), () -> "web request predicate " + operation.getId());
	}
}