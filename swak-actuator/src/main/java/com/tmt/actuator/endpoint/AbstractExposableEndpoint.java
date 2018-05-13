package com.tmt.actuator.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;

public abstract class AbstractExposableEndpoint<O extends Operation> implements ExposableEndpoint<O> {

	private final String id;

	private boolean enabledByDefault;

	private List<O> operations;

	/**
	 * Create a new {@link AbstractExposableEndpoint} instance.
	 * 
	 * @param id
	 *            the endpoint id
	 * @param enabledByDefault
	 *            if the endpoint is enabled by default
	 * @param operations
	 *            the endpoint operations
	 */
	public AbstractExposableEndpoint(String id, boolean enabledByDefault, Collection<? extends O> operations) {
		Assert.notNull(id, "ID must not be null");
		Assert.notNull(operations, "Operations must not be null");
		this.id = id;
		this.enabledByDefault = enabledByDefault;
		this.operations = Collections.unmodifiableList(new ArrayList<>(operations));
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public boolean isEnableByDefault() {
		return this.enabledByDefault;
	}

	@Override
	public Collection<O> getOperations() {
		return this.operations;
	}
}