package com.swak.actuator.endpoint;

public interface Operation {
	
	/**
	 * Invoke the underlying operation using the given {@code context}.
	 * @param context the context in to use when invoking the operation
	 * @return the result of the operation, may be {@code null}
	 */
	Object invoke(InvocationContext context);
}
