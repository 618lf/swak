package com.swak.vertx.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import com.swak.Constants;
import com.swak.annotation.Body;
import com.swak.annotation.FluxService;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.annotation.Server;
import com.swak.annotation.Valid;
import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;
import com.swak.asm.FieldCache.FieldMeta;
import com.swak.exception.ErrorCode;
import com.swak.meters.MetricsFactory;
import com.swak.security.Permission;
import com.swak.utils.JsonMapper;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.validator.Validator;
import com.swak.validator.errors.BindErrors;
import com.swak.vertx.handler.MethodInvoker.MethodParameter;
import com.swak.vertx.security.SecuritySubject;
import com.swak.vertx.transport.Subject;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.VertxThread;
import io.vertx.core.net.impl.ConnectionBase;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 请求执行器, 定义为 http 服务入口
 * 
 * @author lifeng
 */
@FluxService(value = "handlerAdapter", server = Server.Http, instances = -1)
public class HandlerAdapter extends AbstractRouterHandler {

	@Autowired(required = false)
	private Validator validator;
	@Autowired(required = false)
	private MetricsFactory metricsFactory;
	@Autowired
	private ConversionService conversionService;
	@Autowired
	private ResultHandler resultHandler;

	/**
	 * 初始化处理器
	 */
	@Override
	public void initHandler(MethodInvoker handler) {
		MethodParameter[] parameters = handler.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];

			// 实际的类型
			Class<?> parameterType = parameter.getNestedParameterType();

			// 对于集合类型支持第一层
			this.initField(parameterType);
		}

		// 应用监控
		handler.applyMetrics(metricsFactory);
	}

	/**
	 * 组装类型，子类型也一并组装
	 */
	private void initField(Class<?> parameterType) {
		if (parameterType == null || parameterType == HttpServerRequest.class
				|| parameterType == HttpServerResponse.class || parameterType == RoutingContext.class
				|| parameterType == Subject.class || parameterType == BindErrors.class
				|| BeanUtils.isSimpleProperty(parameterType) || Collection.class.isAssignableFrom(parameterType)
				|| List.class.isAssignableFrom(parameterType) || Map.class.isAssignableFrom(parameterType)) {
			return;
		}

		// 不存在的类型需要去解析, 防止死循环
		if (!FieldCache.exists(parameterType)) {
			// 缓存父类型
			FieldCache.set(parameterType);

			// 子类型
			ClassMeta classMeta = FieldCache.get(parameterType);
			classMeta.getFields().values().forEach(field -> {
				this.initField(field.getNestedFieldClass());
			});
		}
	}

	/**
	 * handle 前置处理
	 * 
	 * @param handler
	 * @return
	 */
	private Object preHandle(MethodInvoker handler) {
		return handler.metrics != null ? handler.metrics.begin() : null;
	}

	/**
	 * 处理请求
	 * 
	 * @param context
	 * @param handler
	 */
	@Override
	public void handle(RoutingContext context, MethodInvoker handler) {
		Subject subject = context.get(Constants.SUBJECT_NAME);
		CompletionStage<Boolean> authFuture = this.checkPermissions(subject, handler);
		if (authFuture != null) {
			authFuture.whenComplete((allow, e) -> {
				if (allow) {
					this.dohandler(context, handler);
				} else {
					this.handleResult(ErrorCode.ACCESS_DENIED, e, context, handler, null);
				}
			});
		} else {
			this.dohandler(context, handler);
		}
	}

	/**
	 * 校验权限
	 * 
	 * @param subject
	 * @param handler
	 * @return
	 */
	private CompletionStage<Boolean> checkPermissions(Subject subject, MethodInvoker handler) {

		// must has subject
		if (subject == null) {
			return CompletableFuture.completedFuture(false);
		}

		// first
		CompletionStage<Boolean> authFuture = null;

		// 优先校验权限
		Permission requiresRoles = handler.getRequiresRoles();
		Permission requiresPermissions = handler.getRequiresPermissions();
		if (requiresPermissions != null && requiresPermissions != Permission.NONE) {
			authFuture = subject.isPermitted(requiresPermissions);
		}

		// return
		if (authFuture != null && requiresRoles != null && requiresRoles != Permission.NONE) {
			return authFuture.thenCompose(res -> {
				if (res) {
					return subject.hasRole(requiresRoles);
				}
				return CompletableFuture.completedFuture(res);
			});
		} else if (requiresRoles != null && requiresRoles != Permission.NONE) {
			authFuture = subject.hasRole(requiresRoles);
		}

		// permissions -> roles
		// permissions
		// roles
		// null
		return authFuture;
	}

	/**
	 * 暂时这么处理
	 * 
	 * @param context
	 * @param handler
	 */
	@SuppressWarnings("unchecked")
	private void dohandler(RoutingContext context, MethodInvoker handler) {
		Object metrics = this.preHandle(handler);
		try {
			Object[] params = this.parseParameters(context, handler);
			Object result = this.dohandler(handler, params);
			if (result != null && result instanceof CompletionStage) {
				CompletionStage<Object> resultFuture = (CompletionStage<Object>) result;
				resultFuture.whenComplete((v, e) -> {
					this.handleResultOnContext(v, e, context, handler, metrics);
				});
			} else {
				this.handleResult(result, null, context, handler, metrics);
			}
		} catch (Throwable e) {
			this.handleResult(null, e, context, handler, metrics);
		}
	}

	/**
	 * 执行处理器
	 * 
	 * @param handler
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private Object dohandler(MethodInvoker handler, Object[] params) throws Throwable {
		return handler.doInvoke(params);
	}

	/**
	 * 在之前的 Context 中处理
	 * 
	 * @param result
	 * @param e
	 * @param context
	 */
	private void handleResultOnContext(Object result, Throwable e, RoutingContext context, MethodInvoker handler,
			Object metrics) {
		ContextInternal $currContext = this.getContext(context);
		if ($currContext != null) {
			$currContext.runOnContext((vo) -> {
				this.handleResult(result, e, context, handler, metrics);
			});
		} else {
			this.handleResult(result, e, context, handler, metrics);
		}
	}

	private ContextInternal getContext(RoutingContext context) {
		HttpConnection conn = context.request().connection();
		if (conn instanceof ConnectionBase) {
			ContextInternal $connContext = ((ConnectionBase) conn).getContext();
			ContextInternal $currContext = (ContextInternal) this.getContext();
			if ($connContext != null && ($currContext == null || $connContext != $currContext)) {
				return $connContext;
			}
		}
		return null;
	}

	/**
	 * 3.8.0 之后不能获取 Context
	 * 
	 * @return
	 */
	private ContextInternal getContext() {
		Thread current = Thread.currentThread();
		if (current instanceof VertxThread) {
			return ((VertxThread) current).getContext();
		}
		return null;
	}

	/**
	 * 处理结果
	 * 
	 * @param result
	 * @param e
	 * @param context
	 */
	private void handleResult(Object result, Throwable e, RoutingContext context, MethodInvoker handler,
			Object metrics) {
		try {
			resultHandler.handleResult(result, e, context);
		} finally {
			this.postHandle(handler, metrics, e);
		}
	}

	/**
	 * 后置处理
	 * 
	 * @param handler
	 * @param metrics
	 * @param e
	 */
	@SuppressWarnings("unchecked")
	private void postHandle(MethodInvoker handler, Object metrics, Throwable e) {
		if (metrics != null && handler.metrics != null) {
			handler.metrics.end(metrics, e == null);
		}
	}

	/**
	 * 解析参数
	 * 
	 * @param context
	 * @param handler
	 * @return
	 */
	private Object[] parseParameters(RoutingContext context, MethodInvoker handler) {
		MethodParameter[] parameters = handler.getParameters();
		Object[] args = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];
			args[i] = this.parseParameter(parameter, context);
		}
		return args;
	}

	/**
	 * 支持的参数解析(方便测试)
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
				subject = new SecuritySubject();
				context.put(Constants.SUBJECT_NAME, subject);
			}
			return subject;
		} else if (parameterType == BindErrors.class) {
			return this.getBindErrors(context);
		} else if (parameter.getParameterAnnotations() != null && parameter.getParameterAnnotations().length > 0
				&& !Valid.class.isInstance(parameter.getParameterAnnotations()[0])) {
			return this.resolveAnnotation(parameter, context);
		} else if (BeanUtils.isSimpleProperty(parameterType)) {
			return this.doConvert(context.request().getParam(parameter.getParameterName()), parameterType);
		} else if (List.class.isAssignableFrom(parameterType)) {
			String resolvedName = parameter.getParameterName();
			return context.request().params().getAll(resolvedName);
		} else if (Map.class.isAssignableFrom(parameterType)) {
			return this.parseArguments(context.request().params().iterator());
		}
		return this.resolveObjectAndValidate(parameter, context);
	}

	/**
	 * 解析注解
	 * 
	 * @return
	 */
	private Object resolveAnnotation(MethodParameter parameter, RoutingContext context) {
		Body body = (Body) parameter.getParameterAnnotation(Body.class);
		if (body != null) {
			if (!parameter.getParameterType().isArray() && BeanUtils.isSimpleProperty(parameter.getParameterType())) {
				return this.doConvert(context.getBodyAsString(), parameter.getParameterType());
			}
			return context.getBody().getBytes();
		}
		Json json = (Json) parameter.getParameterAnnotation(Json.class);
		if (json != null) {
			Class<?> fieldClass = parameter.getParameterType();
			if (List.class.isAssignableFrom(fieldClass)) {
				return JsonMapper.fromJsonToList(context.request().getParam(parameter.getParameterName()),
						parameter.getNestedParameterType());
			} else if (Map.class.isAssignableFrom(fieldClass)) {
				return JsonMapper.fromJson(context.request().getParam(parameter.getParameterName()), HashMap.class);
			}
			return JsonMapper.fromJson(context.request().getParam(parameter.getParameterName()), fieldClass);
		}
		Header header = (Header) parameter.getParameterAnnotation(Header.class);
		if (header != null) {
			Class<?> fieldClass = parameter.getParameterType();
			if (Map.class.isAssignableFrom(fieldClass)) {
				return this.parseHeaders(context);
			} else if (List.class.isAssignableFrom(fieldClass)) {
				return context.request().headers().getAll(parameter.getParameterName());
			}
			return this.doConvert(context.request().getHeader(parameter.getParameterName()),
					parameter.getParameterType());
		}
		return null;
	}

	/**
	 * 解析参数并验证
	 * 
	 * @param parameter
	 * @param context
	 * @return
	 */
	private Object resolveObjectAndValidate(MethodParameter parameter, RoutingContext context) {

		// 需要验证
		if (parameter.hasParameterAnnotation(Valid.class) && validator != null) {
			return this.resolveObject(parameter, context, true);
		}

		// 不需要验证
		return this.resolveObject(parameter, context, false);
	}

	/**
	 * 初始化绑定错误
	 * 
	 * @param context
	 * @return
	 */
	private BindErrors getBindErrors(RoutingContext context) {
		BindErrors errors = context.get(Constants.VALIDATE_NAME);
		if (errors == null) {
			errors = BindErrors.of(Maps.newHashMap());
			context.put(Constants.VALIDATE_NAME, errors);
		}
		return errors;
	}

	/**
	 * 直接解析对象参数 （只填充第一层, 第二层）
	 * 
	 * @param type
	 * @param arguments
	 * @return
	 */
	private Object resolveObject(MethodParameter parameter, RoutingContext context, boolean check) {
		Map<String, Object> arguments = this.parseArguments(context.request().params().iterator());
		return this.resolveObject(parameter.getParameterType(), parameter.getParameterName(), arguments, context,
				check);
	}

	/**
	 * 初始化 数据
	 * 
	 * @return
	 */
	public Object resolveObject(Class<?> clazz, String pname, Map<String, Object> arguments, RoutingContext context,
			boolean check) {
		Object obj = null;
		try {
			obj = clazz.newInstance();
			ClassMeta classMeta = FieldCache.get(clazz);
			if (classMeta != null && !arguments.isEmpty()) {
				this.fillObjectValue(obj, classMeta.getFields(), pname, arguments, context, check);
			}
		} catch (Exception e) {
			logger.error("Set field faile");
		}
		return obj;
	}

	// 循环 arguments 更好一点
	@SuppressWarnings("unchecked")
	private void fillObjectValue(Object obj, Map<String, FieldMeta> fields, String paramName,
			Map<String, Object> arguments, RoutingContext context, boolean check)
			throws IllegalArgumentException, IllegalAccessException {

		// 优先使用下一级的数据
		Object values = arguments.containsKey(paramName) ? arguments.get(paramName) : arguments;
		Iterator<Map.Entry<String, Object>> entrys = null;
		if (values == null || !(values instanceof Map)) {
			values = arguments;
		}
		entrys = ((Map<String, Object>) values).entrySet().iterator();

		// 循环设置数据
		while (entrys.hasNext()) {

			// 基础数据
			Map.Entry<String, Object> entry = entrys.next();
			String key = entry.getKey();
			Object value = entry.getValue();

			// 值不能为空
			if (value == null || StringUtils.NULL.equals(value)) {
				continue;
			}

			// 是否可以获取配置
			FieldMeta field = fields.get(key);

			// 不存在的字段，不需要处理
			if (field == null) {
				continue;
			}

			// 设置值
			try {

				// 处理结果
				Object result = null;
				if (field.hasAnnotation(Json.class)) {
					if (value instanceof String) {
						result = this.doJsonMapper(value, field);
					} else {
						result = this.doConvert(value, field.getFieldClass());
					}
				} else if (List.class.isAssignableFrom(field.getFieldClass())) {
					if (value instanceof List) {
						result = this.doConvert(value, field.getFieldClass());
					} else if (value instanceof String) {
						result = this.doConvert(Lists.newArrayList(value), field.getFieldClass());
					} else if (value instanceof Map && BeanUtils.isSimpleProperty(field.getNestedFieldClass())) {
						result = this.doConvert(((Map<String, Object>) value).values(), field.getFieldClass());
					} else if (value instanceof Map && Map.class.isAssignableFrom(field.getNestedFieldClass())) {
						result = this.doConvert(((Map<String, Object>) value).values(), field.getFieldClass());
					} else if (value instanceof Map) {
						result = this.resolveChildObject(field.getNestedFieldClass(), StringUtils.EMPTY,
								(Map<String, Object>) value, context, check);
					}
				} else if (Map.class.isAssignableFrom(field.getFieldClass())) {
					if (value instanceof Map) {
						result = this.doConvert(((Map<String, Object>) value), field.getFieldClass());
					} else {
						result = Maps.newHashMap();
					}
				} else if (BeanUtils.isSimpleProperty(field.getFieldClass())) {
					result = this.doConvert(value, field.getFieldClass());
				} else if (value instanceof Map) {
					result = this.resolveObject(field.getFieldClass(), field.getPropertyName(),
							((Map<String, Object>) values), context, check);
				}

				// 设置值
				field.getField().set(obj, result);

				// 需要校验, 存储校验结构
				String error = null;
				if (check && (error = this.validator.validate(field, result)) != null) {
					BindErrors errors = this.getBindErrors(context);
					errors.addError(paramName, field.getPropertyName(), error);
				}
			} catch (Exception e) {
				logger.error("Request {}, Set Object[{}] field [{}] faile : value [{}] ", context.request().uri(),
						obj.getClass().getName(), field.getPropertyName(), value);
			}
		}
	}

	/**
	 * list 子类型的解析
	 * 
	 * @param arguments
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object resolveChildObject(Class<?> clazz, String pname, Map<String, Object> arguments,
			RoutingContext context, boolean check) {
		List<Object> values = Lists.newArrayList();
		arguments.entrySet().forEach(entry -> {
			Object value = entry.getValue();
			if (value instanceof Map) {
				value = this.resolveObject(clazz, pname, (Map<String, Object>) value, context, check);
				values.add(value);
			}
		});
		return values;
	}

	private Map<String, Object> parseHeaders(RoutingContext request) {
		MultiMap maps = request.request().headers();
		Map<String, Object> arguments = new LinkedHashMap<>();
		maps.forEach(entry -> {
			arguments.put(entry.getKey(), entry.getValue());
		});
		return arguments;
	}

	/**
	 * 解析参数
	 * 
	 * @param entrys
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> parseArguments(Iterator<Map.Entry<String, String>> entrys) {
		Map<String, Object> arguments = new LinkedHashMap<>();
		while (entrys.hasNext()) {
			Map.Entry<String, String> entry = entrys.next();
			String key = entry.getKey();
			String k2 = null;
			String k3 = null;
			String k4 = null;
			if (StringUtils.contains(key, "[")) {
				String[] values = key.split("\\[");
				key = values[0];
				k2 = values.length >= 2 ? values[1].substring(0, values[1].length() - 1) : null;
				k3 = values.length >= 3 ? values[2].substring(0, values[2].length() - 1) : null;
				k4 = values.length >= 4 ? values[3].substring(0, values[3].length() - 1) : null;
			}
			// 四层数据设置
			if (k2 != null && k3 != null && k4 != null) {
				Map<String, Object> k_values = (Map<String, Object>) arguments.get(key);
				if (k_values == null) {
					k_values = Maps.newHashMap();
					arguments.put(key, k_values);
				}
				Map<String, Object> k2_values = (Map<String, Object>) k_values.get(k2);
				if (k2_values == null) {
					k2_values = Maps.newHashMap();
					k_values.put(k2, k2_values);
				}
				Map<String, Object> k3_values = (Map<String, Object>) k2_values.get(k3);
				if (k3_values == null) {
					k3_values = Maps.newHashMap();
					k2_values.put(k3, k3_values);
				}
				this.addMap(k3_values, k4, entry.getValue());
			}
			// 三层数据设置
			else if (k2 != null && k3 != null) {
				Map<String, Object> k_values = (Map<String, Object>) arguments.get(key);
				if (k_values == null) {
					k_values = Maps.newHashMap();
					arguments.put(key, k_values);
				}
				Map<String, Object> k2_values = (Map<String, Object>) k_values.get(k2);
				if (k2_values == null) {
					k2_values = Maps.newHashMap();
					k_values.put(k2, k2_values);
				}
				this.addMap(k2_values, k3, entry.getValue());
			}

			// 两层数据设置
			else if (k2 != null) {
				Map<String, Object> k_values = (Map<String, Object>) arguments.get(key);
				if (k_values == null) {
					k_values = Maps.newHashMap();
					arguments.put(key, k_values);
				}
				this.addMap(k_values, k2, entry.getValue());
			}

			// 一层数据设置, 可以有多个值
			else {
				this.addMap(arguments, key, entry.getValue());
			}
		}
		return arguments;
	}

	/**
	 * 相同的值逐渐升级为 LIST
	 */
	@SuppressWarnings("unchecked")
	protected void addMap(Map<String, Object> arguments, String key, Object value) {
		if (arguments.containsKey(key) && arguments.get(key) instanceof List) {
			List<Object> values = ((List<Object>) arguments.get(key));
			values.add(value);
		} else if (arguments.containsKey(key) && arguments.get(key) instanceof String) {
			List<Object> values = Lists.newArrayList();
			values.add(arguments.get(key));
			values.add(value);
			arguments.put(key, values);
		} else {
			arguments.put(key, value);
		}
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
		if (List.class.isAssignableFrom(fieldClass)) {
			return JsonMapper.fromJsonToList(json.toString(), field.getNestedFieldClass());
		} else if (Map.class.isAssignableFrom(fieldClass)) {
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