package com.swak.vertx.handler;

import java.util.Collection;
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

import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.ServiceMapping;
import com.swak.vertx.security.Subject;
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
				|| parameterType == RoutingContext.class || parameterType == Subject.class || BeanUtils.isSimpleProperty(parameterType)
				|| parameterType.isAssignableFrom(Collection.class) || parameterType.isAssignableFrom(Map.class)) {
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
		Class<?> parameterType = parameter.getParameterType();
		if (parameterType == HttpServerRequest.class) {
			return context.request();
		} else if (parameterType == HttpServerResponse.class) {
			return context.response();
		} else if (parameterType == RoutingContext.class) {
			return context;
		} else if (parameterType == Subject.class) {
			Subject subject = context.get(Subject.SUBJECT_NAME);
			if (subject == null) {
				subject = new Subject();
				context.put(Subject.SUBJECT_NAME, subject);
			}
			return subject;
		}else if (BeanUtils.isSimpleProperty(parameterType)) {
			return this.doConvert(context.request().getParam(parameter.getParameterName()), parameterType);
		} else if (parameterType.isAssignableFrom(List.class)) {
			return Lists.newArrayList();
		} else if (parameterType.isAssignableFrom(Map.class)) {
			return this.getArguments(context);
		}
		return this.resolveObject(parameter, context);
	}

	// /**
	// * 解析集合 xxx[0] xxx[1] xxx[2] xxx[3]
	// * 先写一部分，后面的再研究
	// *
	// * 这个还没有完成
	// *
	// * @param paramtype
	// * @param context
	// * @return
	// */
	// private Object resolveList(MethodParameter parameter, RoutingContext context)
	// {
	// Class<?> parameterType = parameter.getNestedParameterType();
	// // xxx[0] xxx[1] xxx[2] xxx[3]
	// if (BeanUtils.isSimpleProperty(parameterType)) {
	// Pattern pattern = Pattern.compile(new
	// StringBuilder(parameter.getParameterName()).append("\\[").append("\\d+").append("\\]").toString());
	// Map<String, Object> arguments = this.getArguments(context);
	// List<Object> oList = Lists.newArrayList();
	// for (String key : arguments.keySet()) {
	// if (pattern.matcher(key).find()) {
	// oList.add(arguments.get(key));
	// }
	// }
	// }
	// // xxx[0][yyy] xxx[0][yyy] xxx[0][yyy] xxx[0][yyy]
	// // 这部分比较复杂
	// else if(FieldCache.get(parameterType) != null) {
	//
	// }
	// return Lists.newArrayList();
	// }

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
			if (value == null) {
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
			if (field != null) {
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