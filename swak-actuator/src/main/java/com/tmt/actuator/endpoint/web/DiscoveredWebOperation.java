package com.tmt.actuator.endpoint.web;

import java.lang.reflect.Method;

import com.tmt.actuator.endpoint.AbstractDiscoveredOperation;
import com.tmt.actuator.endpoint.annotation.EndpointDiscoverer.EndpointBean;
import com.tmt.actuator.endpoint.invoke.WebOperationInvoker;

public class DiscoveredWebOperation extends AbstractDiscoveredOperation implements WebOperation {

	private final String id;
	
	public DiscoveredWebOperation(EndpointBean endpointBean, WebOperationInvoker invoker) {
		super(invoker);
		this.id = getId(endpointBean.getId(), invoker.getOperationMethod());
	}
	
	private String getId(String endpointId, Method method) {
		return endpointId + method.getName();
	}
	
	public String getId() {
		return id;
	}
}