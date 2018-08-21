package com.swak.actuator.endpoint.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import com.swak.Constants;
import com.swak.actuator.endpoint.annotation.Selector;
import com.swak.utils.Lists;
import com.swak.utils.ParameterNameResolver;

public class OperationMethod {

	private final Object target;
	private final Method method;
	private final List<OperationParameter> parameters;

	public OperationMethod(Object target, Method method) {
		this.target = target;
		this.method = method;
		this.parameters = initMethodParameters();
	}

	private List<OperationParameter> initMethodParameters() {
		int count = this.method.getParameterTypes().length;
		String[] pnames = ParameterNameResolver.resolveParameterName(this.method);
		Parameter[] parameters = this.method.getParameters();
		List<OperationParameter> result = Lists.newArrayList();
		for (int i = 0; i < count; i++) {
			result.add(createParameter(pnames[i], parameters[i]));
		}
		return result;
	}

	private OperationParameter createParameter(String name, Parameter parameter) {
		boolean selector = AnnotatedElementUtils.hasAnnotation(parameter, Selector.class);
		return new OperationParameter(name, parameter, selector);
	}

	public Method getMethod() {
		return method;
	}

	public Object getTarget() {
		return target;
	}

	public List<OperationParameter> getParameters() {
		return parameters;
	}

	/**
	 * web flux 和 vertx 的处理方式不一样
	 * 
	 * @return
	 */
	public String getPath() {
		return new StringBuilder().append(method.getName())
				.append(parameters.stream().filter(parameter -> parameter.isSelector())
						.map(parameter -> this.parsePath(parameter.getName())).collect(Collectors.joining()))
				.toString();
	}

	private String parsePath(String name) {
		if (!ClassUtils.isPresent("com.swak.vertx.transport.ReactiveServer", null)) {
			return new StringBuilder(Constants.URL_PATH_SEPARATE).append(Constants.URL_PATH_VARIABLE_PRE).append(name)
					.append(Constants.URL_PATH_VARIABLE_SUFFIX).toString();
		}
		return new StringBuilder(Constants.URL_PATH_SEPARATE).append(Constants._URL_PATH_VARIABLE_PRE).append(name)
				.append(Constants._URL_PATH_VARIABLE_SUFFIX).toString();
	}
}