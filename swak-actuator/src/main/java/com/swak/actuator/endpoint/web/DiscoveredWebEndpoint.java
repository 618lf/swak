package com.swak.actuator.endpoint.web;

import java.util.Collection;

import com.swak.actuator.endpoint.AbstractDiscoveredEndpoint;
import com.swak.actuator.endpoint.EndpointDiscoverer;
import com.swak.actuator.endpoint.EndpointDiscoverer.EndpointBean;

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