package com.swak.actuator.endpoint;

import com.swak.actuator.endpoint.annotation.EndpointDiscoverer;

public interface DiscoveredEndpoint<O extends Operation> extends ExposableEndpoint<O> {

	/**
	 * Return {@code true} if the endpoint was discovered by the specified discoverer.
	 * @param discoverer the discoverer type
	 * @return {@code true} if discovered using the specified discoverer
	 */
	boolean wasDiscoveredBy(Class<? extends EndpointDiscoverer<?, ?>> discoverer);

	/**
	 * Return the source bean that was used to construct the {@link DiscoveredEndpoint}.
	 * @return the source endpoint bean
	 */
	Object getEndpointBean();
}
