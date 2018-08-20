package com.swak.vertx.handler;

import java.util.concurrent.CompletionStage;

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
public class HandlerAdapter {

	@Autowired
	private ConversionService conversionService;
	@Autowired
	private ResultHandler resultHandler;

	/**
	 * 处理请求
	 * 
	 * @param context
	 * @param handler
	 */
	@SuppressWarnings("unchecked")
	public void handle(RoutingContext context, MethodHandler handler) {
		try {
			Object[] params = this.parseParameters(context, handler);
			Object result = handler.doInvoke(params);
			if (result != null && result instanceof CompletionStage) {
				CompletionStage<Object> resultFuture = (CompletionStage<Object>) result;
				resultFuture.whenComplete((v, e) -> {
					resultHandler.handlResult(v, e, context);
				});
			} else {
				resultHandler.handlResult(result, null, context);
			}
		} catch (Exception e) {
			resultHandler.handlError(e, context);
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