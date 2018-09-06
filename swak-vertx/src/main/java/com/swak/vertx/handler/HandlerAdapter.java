package com.swak.vertx.handler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import com.swak.vertx.annotation.ServiceMapping;
import com.swak.vertx.utils.FieldCache;
import com.swak.vertx.utils.FieldCache.ClassMeta;
import com.swak.vertx.utils.FieldCache.FieldMeta;

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
     * 初始化处理器
     */
	@Override
	public void initHandler(MethodHandler handler) {
		MethodParameter[] parameters = handler.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];
			Class<?> parameterType = parameter.getNestedParameterType();
			if (!(parameterType == HttpServerRequest.class
					|| parameterType == HttpServerResponse.class
					|| parameterType == RoutingContext.class
					|| BeanUtils.isSimpleProperty(parameterType))) {
				
				// 预加载需要解析的类型
				FieldCache.set(parameterType);
			}
		}
	}

	/**
	 * 处理请求
	 * 
	 * @param context
	 * @param handler
	 */
	@Override
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
				ClassMeta classMeta = FieldCache.get(paramtype);
				this.fillObjectValue(obj, classMeta.getFields(), arguments);
			}
			return obj;
		} catch (Exception e) {
		}
		return null;
	}
	
	private void fillObjectValue(Object obj, List<FieldMeta> fields, Map<String, Object> arguments) throws IllegalArgumentException, IllegalAccessException {
		for (FieldMeta field : fields) {
			field.getField().setAccessible(true);
			if ("serialVersionUID".equals(field.getPropertyName())) {
				continue;
			}
			Object value = arguments.get(field.getPropertyName());
			if (value != null) {
				field.getField().set(obj, this.doConvert(value, field.getFieldClass()));
			}
		}
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