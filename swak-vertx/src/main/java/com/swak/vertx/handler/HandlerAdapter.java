package com.swak.vertx.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import com.swak.Constants;
import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;
import com.swak.asm.FieldCache.FieldMeta;
import com.swak.utils.JsonMapper;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.VertxService;
import com.swak.vertx.handler.validate.BindErrors;
import com.swak.vertx.security.Subject;

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
@VertxService(value = "handlerAdapter", httpServer = true, instances = -1)
public class HandlerAdapter extends AbstractRouterHandler {

	@Autowired
	private ConversionService conversionService;
	@Autowired
	private ResultHandler resultHandler;
	private Pattern OBJECT_PARAM_PATTERN = Pattern.compile("\\w+\\[(\\w+)\\]");

	/**
	 * 初始化处理器
	 */
	@Override
	public void initHandler(MethodHandler handler) {
		MethodParameter[] parameters = handler.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];

			// 实际的类型
			Class<?> parameterType = parameter.getNestedParameterType();

			// 对于集合类型支持第一层
			this.initField(parameterType);
		}
	}

	private void initField(Class<?> parameterType) {
		if (parameterType == HttpServerRequest.class || parameterType == HttpServerResponse.class
				|| parameterType == RoutingContext.class || parameterType == Subject.class
				|| BeanUtils.isSimpleProperty(parameterType) || parameterType.isAssignableFrom(Collection.class)
				|| parameterType.isAssignableFrom(Map.class)) {
			return;
		}
		FieldCache.set(parameterType);
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

	/**
	 * 解析参数
	 * 
	 * @param context
	 * @param handler
	 * @return
	 */
	private Object[] parseParameters(RoutingContext context, MethodHandler handler) {
		MethodParameter[] parameters = handler.getParameters();
		Object[] args = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];
			args[i] = this.parseParameter(parameter, context);
		}
		return args;
	}

	/**
	 * 支持的参数解析
	 * 
	 * @param parameter
	 * @param context
	 * @return
	 */
	private Object parseParameter(MethodParameter parameter, RoutingContext context) {
		Class<?> parameterType = parameter.getParameterType();
		if (parameterType == HttpServerRequest.class) {
			return context.request();
		} else if (parameterType == HttpServerResponse.class) {
			return context.response();
		} else if (parameterType == RoutingContext.class) {
			return context;
		} else if (parameterType == Subject.class) {
			Subject subject = context.get(Constants.SUBJECT_NAME);
			if (subject == null) {
				subject = new Subject();
				context.put(Constants.SUBJECT_NAME, subject);
			}
			return subject;
		} else if (parameterType == BindErrors.class) {
			BindErrors errors = context.get(Constants.VALIDATE_NAME);
			return errors == null ? BindErrors.NONE : errors;
		} else if (BeanUtils.isSimpleProperty(parameterType)) {
			return this.doConvert(context.request().getParam(parameter.getParameterName()), parameterType);
		} else if (parameterType.isAssignableFrom(List.class)) {
			return Lists.newArrayList();
		} else if (parameterType.isAssignableFrom(Map.class)) {
			return this.getArguments(context);
		}
		return this.resolveObjectAndValidate(parameter, context);
	}

	/**
	 * 解析参数并验证
	 * 
	 * @param parameter
	 * @param context
	 * @return
	 */
	private Object resolveObjectAndValidate(MethodParameter parameter, RoutingContext context) {

		// 解析参数
		Object result = this.resolveObject(parameter, context);

//		// 如果标示了需要验证参数
//		if (parameter.hasParameterAnnotation(Valid.class) && validator != null) {
//			BindErrors errors = this.validateObject(result, context);
//			context.put(Constants.VALIDATE_NAME, errors);
//		}

		// 返回参数
		return result;
	}

//	private BindErrors validateObject(Object result, RoutingContext context) {
//		Set<ConstraintViolation<Object>> errors = validator.validate(result);
//		if (errors != null && !errors.isEmpty()) {
//			Map<String, String> errorMessages = Maps.newHashMap();
//			for (ConstraintViolation<Object> violation : errors) {
//				errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
//			}
//			return BindErrors.of(errorMessages);
//		}
//		return null;
//	}

	/**
	 * 直接解析对象参数 （只填充第一层）
	 * 
	 * @param type
	 * @param arguments
	 * @return
	 */
	private Object resolveObject(MethodParameter parameter, RoutingContext context) {
		ClassMeta classMeta = FieldCache.get(parameter.getParameterType());
		if (classMeta == null) {
			return null;
		}
		try {
			Map<String, Object> arguments = this.getArguments(context);
			Object obj = parameter.getParameterType().newInstance();
			if (!arguments.isEmpty()) {
				classMeta = FieldCache.get(parameter.getParameterType());
				this.fillObjectValue(obj, classMeta.getFields(), parameter.getParameterName(), arguments);
			}
			return obj;
		} catch (Exception e) {
			logger.error("Set obj field faile");
		}
		return null;
	}

	// 循环 arguments 更好一点
	private void fillObjectValue(Object obj, Map<String, FieldMeta> fields, String paramName,
			Map<String, Object> arguments) throws IllegalArgumentException, IllegalAccessException {
		Iterator<String> it = arguments.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value = arguments.get(key);

			// 值不能为空
			if (value == null || StringUtils.NULL.equals(value)) {
				continue;
			}

			// 是否可以获取配置
			FieldMeta field = fields.get(key);
			Matcher found = null;

			// name[n]=xxx 的型式，如果 name 和 参数名称一致也填充进去
			if (field == null && StringUtils.startsWith(key, paramName) && StringUtils.endsWith(key, "]")
					&& (found = OBJECT_PARAM_PATTERN.matcher(key)).find()) {
				key = found.group(1);
				field = fields.get(key);
			}

			// 设置值
			try {
				if (field != null && field.isJson()) {
					field.getField().set(obj, this.doJsonMapper(value, field));
				} else if (field != null) {
					field.getField().set(obj, this.doConvert(value, field.getFieldClass()));
				}
			} catch (Exception e) {
				logger.error("Set obj field faile:field[{}]-value[{}]", field.getPropertyName(), value);
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
	 * 执行JSON转换
	 * 
	 * @param value
	 * @param field
	 * @return
	 */
	protected Object doJsonMapper(Object json, FieldMeta field) {
		Class<?> fieldClass = field.getFieldClass();
		if (fieldClass.isAssignableFrom(List.class)) {
			return JsonMapper.fromJsonToList(json.toString(), field.getNestedFieldClass());
		} else if (fieldClass.isAssignableFrom(Map.class)) {
			return JsonMapper.fromJson(json.toString(), HashMap.class);
		}
		return JsonMapper.fromJson(json.toString(), field.getFieldClass());
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