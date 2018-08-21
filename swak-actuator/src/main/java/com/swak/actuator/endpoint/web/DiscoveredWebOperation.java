package com.swak.actuator.endpoint.web;

import com.swak.Constants;
import com.swak.actuator.endpoint.AbstractDiscoveredOperation;
import com.swak.actuator.endpoint.EndpointDiscoverer.EndpointBean;
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
		String path = operationMethod.getPath();
		if (StringUtils.equalsIgnoreCase(path, endpointId)) {
			return path;
		}
		return new StringBuilder(endpointId).append(Constants.URL_PATH_SEPARATE).append(path).toString();
	}
	
	public String getId() {
		return id;
	}
}