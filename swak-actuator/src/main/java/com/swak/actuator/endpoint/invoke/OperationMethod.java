package com.swak.actuator.endpoint.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotatedElementUtils;

import com.swak.Constants;
import com.swak.actuator.endpoint.annotation.Selector;
import com.swak.reactivex.web.method.ParameterNameResolver;
import com.swak.utils.Lists;

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

	public String getPath() {
		return new StringBuilder().append(method.getName())
				.append(parameters.stream().filter(parameter -> parameter.isSelector())
				.map(parameter -> this.parsePath(parameter.getName())).collect(Collectors.joining())).toString();
	}
	
	private String parsePath(String name) {
		return new StringBuilder(Constants.URL_PATH_SEPARATE).append("{").append(name).append("}").toString();
	}
}