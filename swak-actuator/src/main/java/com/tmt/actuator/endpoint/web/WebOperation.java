package com.tmt.actuator.endpoint.web;

import com.tmt.actuator.endpoint.Operation;

public interface WebOperation extends Operation {

	/**
	 * Returns the ID of the operation that uniquely identifies it within its endpoint.
	 * @return the ID
	 */
	String getId();
}
