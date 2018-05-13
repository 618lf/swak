package com.tmt.actuator.endpoint.invoke;

import com.tmt.actuator.endpoint.InvocationContext;

@FunctionalInterface
public interface OperationInvoker {

	/**
	 * Invoke the underlying operation using the given {@code context}.
	 * @param context the context to use to invoke the operation
	 * @return the result of the operation, may be {@code null}
	 */
	Object invoke(InvocationContext context);
}
