package com.tmt.actuator.endpoint.web;

import com.tmt.actuator.endpoint.ExposableEndpoint;

public interface ExposableWebEndpoint extends ExposableEndpoint<WebOperation> {

	/**
	 * Return the root path of the endpoint, relative to the context that exposes
	 * it. For example, a root path of {@code example} would be exposed under the
	 * URL "/{actuator-context}/example".
	 * 
	 * @return the root path for the endpoint
	 */
	String getRootPath();
}
