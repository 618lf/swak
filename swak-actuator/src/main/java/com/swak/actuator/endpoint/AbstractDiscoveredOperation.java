package com.swak.actuator.endpoint;

import org.springframework.core.style.ToStringCreator;

import com.swak.actuator.endpoint.invoke.OperationInvoker;

public abstract class AbstractDiscoveredOperation implements Operation {

	private final OperationInvoker invoker;
	
	/**
	 * Create a new {@link AbstractDiscoveredOperation} instance.
	 * @param operationMethod the method backing the operation
	 * @param invoker the operation invoker to use
	 */
	public AbstractDiscoveredOperation(OperationInvoker invoker) {
		this.invoker = invoker;
	}
	
	@Override
	public Object invoke(InvocationContext context) {
		return this.invoker.invoke(context);
	}

	@Override
	public String toString() {
		ToStringCreator creator = new ToStringCreator(this)
				.append("invoker", this.invoker);
		appendFields(creator);
		return creator.toString();
	}

	protected void appendFields(ToStringCreator creator) {
	}
}
