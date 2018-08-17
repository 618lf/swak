package com.swak.vertx.router;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * 请求执行器
 * 
 * @author lifeng
 */
public class RequestMappingRouterAdapter {

	@Autowired
	private ConversionService conversionService;

	public void handle(RoutingContext context, MethodHandler handler) {
		try {
			Object[] params = this.parseParameters(context, handler);
			handler.doInvoke(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object[] parseParameters(RoutingContext context, MethodHandler handler) {
		MethodParameter[] parameters = handler.getParameters();
		Object[] args = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];
			args[i] = this.parseParameter(parameter, context);
		}
		return args;
	}

	private Object parseParameter(MethodParameter parameter, RoutingContext context) {
		Object value = null;
		Class<?> parameterType = parameter.getNestedParameterType();
		if (parameterType == HttpServerRequest.class) {
			value = context.request();
		} else if (parameterType == HttpServerResponse.class) {
			value = context.response();
		} else if (parameterType == RoutingContext.class) {
			value = context;
		} else if (BeanUtils.isSimpleProperty(parameterType)) {
			value = context.request().getParam(parameter.getParameterName());
		}
		return this.doConvert(value, parameterType);
	}

	/**
	 * 执行转换
	 * 
	 * @param value
	 * @param targetType
	 * @return
	 */
	protected Object doConvert(Object value, Class<?> targetType) {
		TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(value);
		TypeDescriptor targetDescriptor = TypeDescriptor.valueOf(targetType);
		if (conversionService.canConvert(sourceTypeDesc, targetDescriptor)) {
			return conversionService.convert(value, sourceTypeDesc, targetDescriptor);
		}
		return null;
	}
}