package com.tmt.actuator.endpoint.web;

import java.util.Collection;

import com.tmt.actuator.endpoint.AbstractDiscoveredEndpoint;
import com.tmt.actuator.endpoint.annotation.EndpointDiscoverer;
import com.tmt.actuator.endpoint.annotation.EndpointDiscoverer.EndpointBean;

public class DiscoveredWebEndpoint extends AbstractDiscoveredEndpoint<WebOperation> implements ExposableWebEndpoint {

	private final String rootPath;
	
	public DiscoveredWebEndpoint(EndpointDiscoverer<?, ?> discoverer, EndpointBean endpointBean,
			String rootPath, Collection<? extends WebOperation> operations) {
		super(discoverer, endpointBean, operations);
		this.rootPath = rootPath;
	}
	
	@Override
	public String getRootPath() {
		return this.rootPath;
	}
}