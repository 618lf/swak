package com.swak.actuator.endpoint.invoke;

import java.lang.reflect.Method;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.swak.actuator.endpoint.InvocationContext;

public class ReflectiveOperationInvoker implements OperationInvoker {

	private final OperationParameterResoler operationParameterResoler;
	private final OperationMethod operationMethod;
	
	public ReflectiveOperationInvoker(OperationParameterResoler operationParameterResoler, OperationMethod operationMethod) {
		Assert.notNull(operationMethod, "OperationMethod must not be null");
		ReflectionUtils.makeAccessible(operationMethod.getMethod());
		this.operationParameterResoler = operationParameterResoler;
		this.operationMethod = operationMethod;
	}
	
	public OperationMethod getOperationMethod() {
		return operationMethod;
	}

	@Override
	public Object invoke(InvocationContext context) {
		Method method = this.operationMethod.getMethod();
		Object target = this.operationMethod.getTarget();
		Object[] resolvedArguments = resolveArguments(context);
		return ReflectionUtils.invokeMethod(method, target, resolvedArguments);
	}
	
	/**
	 * 获取参数
	 * @param context
	 * @return
	 */
	private Object[] resolveArguments(InvocationContext context) {
		return this.operationMethod.getParameters().stream().map(parameter -> this.resolveArguments(parameter, context)).toArray();
	}
	
	/**
	 * 获取参数
	 * @param context
	 * @return
	 */
	private Object resolveArguments(OperationParameter parameter, InvocationContext context) {
		Object value = context.getArguments().get(parameter.getName());
		return this.operationParameterResoler.doConvert(value, parameter.getType());
	}
}
