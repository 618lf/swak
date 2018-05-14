package com.swak.actuator.endpoint.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.swak.actuator.endpoint.InvocationContext;
import com.swak.common.utils.Lists;
import com.swak.reactivex.web.method.ParameterNameResolver;

public class ReflectiveOperationInvoker implements OperationInvoker {

	private final OperationParameterResoler operationParameterResoler;
	private final Object target;
	private final Method method;
	private final List<OperationParameter> parameters;
	
	public ReflectiveOperationInvoker(OperationParameterResoler operationParameterResoler, Object target, Method operationMethod) {
		Assert.notNull(target, "Target must not be null");
		Assert.notNull(operationMethod, "OperationMethod must not be null");
		ReflectionUtils.makeAccessible(operationMethod);
		this.operationParameterResoler = operationParameterResoler;
		this.target = target;
		this.method = operationMethod;
		this.parameters = initMethodParameters();
	}
	
	private List<OperationParameter> initMethodParameters() {
		int count = this.method.getParameterTypes().length;
		String[] pnames = ParameterNameResolver.resolveParameterName(method);
		Parameter[] parameters = method.getParameters();
		List<OperationParameter> result =Lists.newArrayList();
		for (int i = 0; i < count; i++) {
			result.add(new OperationParameter(pnames[i], parameters[i]));
		}
		return result;
	}
	
	public Object getTarget() {
		return target;
	}

	public Method getOperationMethod() {
		return method;
	}

	@Override
	public Object invoke(InvocationContext context) {
		Object[] resolvedArguments = resolveArguments(context);
		return ReflectionUtils.invokeMethod(method, this.target, resolvedArguments);
	}
	
	/**
	 * 获取参数
	 * @param context
	 * @return
	 */
	private Object[] resolveArguments(InvocationContext context) {
		return this.parameters.stream().map(parameter -> this.resolveArguments(parameter, context)).toArray();
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
	
	@Override
	public String toString() {
		return new ToStringCreator(this).append("target", this.target)
				.append("method", this.method).toString();
	}
}
