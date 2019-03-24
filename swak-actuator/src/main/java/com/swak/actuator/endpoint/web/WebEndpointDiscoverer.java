package com.swak.actuator.endpoint.web;

import java.util.Collection;

import com.swak.actuator.endpoint.EndpointDiscoverer;
import com.swak.actuator.endpoint.invoke.OperationParameterResoler;
import com.swak.actuator.endpoint.invoke.ReflectiveOperationInvoker;

/**
 * 基于web 的 endpoint
 * 
 * @author lifeng
 */
public class WebEndpointDiscoverer extends EndpointDiscoverer<ExposableWebEndpoint, WebOperation>
		implements WebEndpointsSupplier {

	private final String rootPath;

	public WebEndpointDiscoverer(String rootPath, OperationParameterResoler operationParameterResoler) {
		super(operationParameterResoler);
		this.rootPath = rootPath;
	}

	@Override
	public String getRootPath() {
		return this.rootPath;
	}

	@Override
	protected ExposableWebEndpoint createEndpoint(EndpointBean endpointBean, Collection<WebOperation> operations) {
		return new DiscoveredWebEndpoint(this, endpointBean, rootPath, operations);
	}

	@Override
	protected WebOperation createOperation(EndpointBean endpointBean, ReflectiveOperationInvoker invoker) {
		return new DiscoveredWebOperation(endpointBean, invoker);
	}

	@Override
	protected OperationKey createOperationKey(WebOperation operation) {
		return new OperationKey(operation.getId(), () -> "web request predicate " + operation.getId());
	}
}