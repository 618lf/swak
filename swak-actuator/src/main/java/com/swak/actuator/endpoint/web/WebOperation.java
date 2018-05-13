package com.swak.actuator.endpoint.web;

import com.swak.actuator.endpoint.Operation;

public interface WebOperation extends Operation {

	/**
	 * Returns the ID of the operation that uniquely identifies it within its endpoint.
	 * @return the ID
	 */
	String getId();
}
