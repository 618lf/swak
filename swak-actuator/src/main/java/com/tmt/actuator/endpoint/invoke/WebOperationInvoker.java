package com.tmt.actuator.endpoint.invoke;

import java.lang.reflect.Method;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.tmt.actuator.endpoint.InvocationContext;

public class WebOperationInvoker implements OperationInvoker {

	private final Object target;

	private final Method operationMethod;
	
	public WebOperationInvoker(Object target, Method operationMethod) {
		Assert.notNull(target, "Target must not be null");
		Assert.notNull(operationMethod, "OperationMethod must not be null");
		ReflectionUtils.makeAccessible(operationMethod);
		this.target = target;
		this.operationMethod = operationMethod;
	}
	
	public Object getTarget() {
		return target;
	}

	public Method getOperationMethod() {
		return operationMethod;
	}

	@Override
	public Object invoke(InvocationContext context) {
		Method method = operationMethod;
		Object[] resolvedArguments = resolveArguments(context);
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invokeMethod(method, this.target, resolvedArguments);
	}
	
	private Object[] resolveArguments(InvocationContext context) {
		return new Object[] {};
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("target", this.target)
				.append("method", this.operationMethod).toString();
	}
}
