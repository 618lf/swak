package com.swak.actuator.endpoint.invoke;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.swak.actuator.endpoint.InvocationContext;

public class ReflectiveOperationInvoker implements OperationInvoker {

	private final OperationParameterResoler operationParameterResoler;
	private final OperationMethod operationMethod;

	public ReflectiveOperationInvoker(OperationParameterResoler operationParameterResoler,
			OperationMethod operationMethod) {
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
	 * 
	 * @param context
	 * @return
	 */
	private Object[] resolveArguments(InvocationContext context) {
		return this.operationMethod.getParameters().stream().map(parameter -> this.resolveArguments(parameter, context))
				.toArray();
	}

	/**
	 * 可以设置的参数
	 * 
	 * HttpServerRequest Map<String, Object> map 基本类型 复杂类型（只做一层）
	 * 
	 * @param parameter
	 * @param arguments
	 * @return
	 */
	private Object resolveArguments(OperationParameter parameter, InvocationContext context) {
		Class<?> type = parameter.getType();
		if (type == context.getClass()) {
			return context.getRequest();
		} else if (BeanUtils.isSimpleProperty(type)) {
			Object value = context.getArguments().get(parameter.getName());
			return this.operationParameterResoler.doConvert(value, type);
		}
		return resolveObject(type, context.getArguments());
	}

	/**
	 * 直接解析对象参数
	 * @param type
	 * @param arguments
	 * @return
	 */
	private Object resolveObject(Class<?> type, Map<String, Object> arguments) {
		try {
			Object obj = type.getDeclaredConstructor().newInstance();
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if ("serialVersionUID".equals(field.getName())) {
					continue;
				}
				Object value = arguments.get(field.getName());
				if (value != null) {
					field.set(obj, this.operationParameterResoler.doConvert(value, field.getType()));
				}
			}
			return obj;
		}catch (Exception e) {}
		return null;
	}
}