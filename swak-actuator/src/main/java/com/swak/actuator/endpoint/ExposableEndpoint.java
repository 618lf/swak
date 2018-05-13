package com.swak.actuator.endpoint;

import java.util.Collection;

public interface ExposableEndpoint<O extends Operation> {

	/**
	 * Returns the id of the endpoint.
	 * @return the id
	 */
	String getId();

	/**
	 * Returns if the endpoint is enabled by default.
	 * @return if the endpoint is enabled by default
	 */
	boolean isEnableByDefault();

	/**
	 * Returns the operations of the endpoint.
	 * @return the operations
	 */
	Collection<O> getOperations();
}
