package com.tmt.actuator.endpoint.web;

import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.context.ApplicationContext;

import com.tmt.actuator.endpoint.annotation.EndpointDiscoverer;
import com.tmt.actuator.endpoint.invoke.WebOperationInvoker;

/**
 * 基于web 的 endpoint
 * @author lifeng
 */
public class WebEndpointDiscoverer extends EndpointDiscoverer<ExposableWebEndpoint, WebOperation>{

	public WebEndpointDiscoverer(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@Override
	protected ExposableWebEndpoint createEndpoint(EndpointBean endpointBean, Collection<WebOperation> operations) {
		return new DiscoveredWebEndpoint(this, endpointBean, "",  operations);
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