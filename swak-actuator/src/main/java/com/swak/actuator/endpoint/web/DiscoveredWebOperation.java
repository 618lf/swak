package com.swak.actuator.endpoint.web;

import com.swak.Constants;
import com.swak.actuator.endpoint.AbstractDiscoveredOperation;
import com.swak.actuator.endpoint.annotation.EndpointDiscoverer.EndpointBean;
import com.swak.actuator.endpoint.invoke.OperationMethod;
import com.swak.actuator.endpoint.invoke.ReflectiveOperationInvoker;
import com.swak.utils.StringUtils;

public class DiscoveredWebOperation extends AbstractDiscoveredOperation implements WebOperation {

	private final String id;
	
	public DiscoveredWebOperation(EndpointBean endpointBean, ReflectiveOperationInvoker invoker) {
		super(invoker);
		this.id = getId(endpointBean.getId(), invoker.getOperationMethod());
	}
	
	private String getId(String endpointId, OperationMethod operationMethod) {
		if (StringUtils.equalsIgnoreCase(operationMethod.getPath(), endpointId)) {
			return operationMethod.getPath();
		}
		return new StringBuilder(endpointId).append(Constants.URL_PATH_SEPARATE).append(operationMethod.getPath()).toString();
	}
	
	public String getId() {
		return id;
	}
}