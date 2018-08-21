package com.swak.actuator.endpoint.web;

import com.swak.actuator.endpoint.EndpointsSupplier;

/**
 * 
 * @author lifeng
 */
public interface WebEndpointsSupplier extends EndpointsSupplier<ExposableWebEndpoint>{
	
	/**
	 * Return the root path of the endpoint, relative to the context that exposes
	 * it. For example, a root path of {@code example} would be exposed under the
	 * URL "/{actuator-context}/example".
	 * 
	 * @return the root path for the endpoint
	 */
	String getRootPath();
}
