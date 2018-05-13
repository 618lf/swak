package com.tmt.actuator.endpoint;

import java.util.Collection;

/**
 * Provides access to a collection of {@link ExposableEndpoint endpoints}.
 * @author lifeng
 *
 * @param <E>
 */
public interface EndpointsSupplier<E extends ExposableEndpoint<?>> {

	/**
	 * Return the provided endpoints.
	 * @return the endpoints
	 */
	Collection<E> getEndpoints();
}
