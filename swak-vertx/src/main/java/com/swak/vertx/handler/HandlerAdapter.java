package com.swak.vertx.handler;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import com.swak.vertx.annotation.ServiceMapping;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 请求执行器, 定义为 http 服务入口
 * 
 * @author lifeng
 */
@ServiceMapping(value = "handlerAdapter", httpServer = true, instances = -1)
public class HandlerAdapter implements RouterHandler {

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
		Class<?> parameterType = parameter.getNestedParameterType();
		if (parameterType == HttpServerRequest.class) {
			return context.request();
		} else if (parameterType == HttpServerResponse.class) {
			return context.response();
		} else if (parameterType == RoutingContext.class) {
			return context;
		} else if (BeanUtils.isSimpleProperty(parameterType)) {
			return this.doConvert(context.request().getParam(parameter.getParameterName()), parameterType);
		}
		return this.resolveObject(parameterType, context);
	}

	/**
	 * 直接解析对象参数
	 * 
	 * @param type
	 * @param arguments
	 * @return
	 */
	private Object resolveObject(Class<?> paramtype, RoutingContext context) {
		try {
			Map<String, Object> arguments = this.getArguments(context);
			Object obj = paramtype.newInstance();
			if (!arguments.isEmpty()) {
				Field[] fields = paramtype.getFields();
				for (Field field : fields) {
					field.setAccessible(true);
					if ("serialVersionUID".equals(field.getName())) {
						continue;
					}
					Object value = arguments.get(field.getName());
					if (value != null) {
						field.set(obj, this.doConvert(value, field.getType()));
					}
				}
			}
			return obj;
		} catch (Exception e) {
		}
		return null;
	}

	private Map<String, Object> getArguments(RoutingContext request) {
		MultiMap maps = request.request().params();
		Map<String, Object> arguments = new LinkedHashMap<>();
		maps.forEach(entry -> {
			arguments.put(entry.getKey(), entry.getValue());
		});
		return arguments;
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