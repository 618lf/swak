package com.swak.vertx.protocol.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import com.swak.Constants;
import com.swak.annotation.Body;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;
import com.swak.asm.FieldCache.FieldMeta;
import com.swak.exception.ErrorCode;
import com.swak.security.Permission;
import com.swak.security.Subject;
import com.swak.utils.JsonMapper;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.validator.Validator;
import com.swak.validator.errors.BindErrors;
import com.swak.vertx.invoker.MethodInvoker;
import com.swak.vertx.invoker.MethodInvoker.MethodParameter;
import com.swak.vertx.security.SecuritySubject;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.VertxThread;
import io.vertx.core.net.impl.ConnectionBase;
import io.vertx.ext.web.RoutingContext;

/**
 * 请求执行器, 定义为 http 服务入口
 *
 * @author: lifeng
 * @date: 2020/3/29 19:48
 */
public class RouterHandlerAdapter implements RouterHandler {

	protected Logger logger = LoggerFactory.getLogger(RouterHandler.class);

	@Autowired(required = false)
	private Validator validator;
	@Autowired
	private ConversionService conversionService;
	@Autowired
	private ResultHandler resultHandler;

	/**
	 * handle 前置处理
	 */
	private Object preHandle(MethodInvoker handler) {
		return handler.metrics != null ? handler.metrics.begin() : null;
	}

	/**
	 * 处理请求
	 */
	@Override
	public void handle(RoutingContext context, MethodInvoker handler) {
		Subject subject = context.get(Constants.SUBJECT_NAME);
		CompletionStage<Boolean> authFuture = this.checkPermissions(subject, handler);
		if (authFuture != null) {
			authFuture.whenComplete((allow, e) -> {
				if (allow) {
					this.doHandler(context, handler);
				} else {
					this.handleResult(ErrorCode.ACCESS_DENIED, e, context, handler, null);
				}
			});
		} else {
			this.doHandler(context, handler);
		}
	}

	/**
	 * 校验权限
	 */
	private CompletionStage<Boolean> checkPermissions(Subject subject, MethodInvoker handler) {

		// 如果没有设置subject，则不用验证
		if (subject == null) {
			return CompletableFuture.completedFuture(true);
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
				return CompletableFuture.completedFuture(false);
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
	 * 执行处理
	 */
	@SuppressWarnings("unchecked")
	private void doHandler(RoutingContext context, MethodInvoker handler) {
		Object metrics = this.preHandle(handler);
		try {
			Object[] params = this.parseParameters(context, handler);
			Object result = this.doHandler(handler, params);
			if (result != null && result instanceof CompletionStage) {
				CompletionStage<Object> resultFuture = (CompletionStage<Object>) result;
				resultFuture.whenComplete((v, e) -> this.handleResultOnContext(v, e, context, handler, metrics));
			} else {
				this.handleResult(result, null, context, handler, metrics);
			}
		} catch (Throwable e) {
			this.handleResult(null, e, context, handler, metrics);
		}
	}

	/**
	 * 执行处理器
	 */
	private Object doHandler(MethodInvoker handler, Object[] params) throws Throwable {
		return handler.doInvoke(params);
	}

	/**
	 * 在之前的 Context 中处理
	 */
	private void handleResultOnContext(Object result, Throwable e, RoutingContext context, MethodInvoker handler,
			Object metrics) {
		ContextInternal currContext = this.getContext(context);
		if (currContext != null) {
			currContext.runOnContext((vo) -> this.handleResult(result, e, context, handler, metrics));
		} else {
			this.handleResult(result, e, context, handler, metrics);
		}
	}

	private ContextInternal getContext(RoutingContext context) {
		HttpConnection conn = context.request().connection();
		if (conn instanceof ConnectionBase) {
			ContextInternal connContext = ((ConnectionBase) conn).getContext();
			ContextInternal currContext = this.getContext();
			return (connContext != null && (currContext == null || connContext != currContext)) ? connContext : null;
		}
		return null;
	}

	/**
	 * 3.8.0 之后不能获取 Context
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
	 */
	@SuppressWarnings("unchecked")
	private void postHandle(MethodInvoker handler, Object metrics, Throwable e) {
		if (metrics != null && handler.metrics != null) {
			handler.metrics.end(metrics, e == null);
		}
	}

	/**
	 * 解析参数
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
	 */
	private Object parseParameter(MethodParameter parameter, RoutingContext context) {
		Class<?> parameterType = parameter.getParameterType();
		if (HttpServerRequest.class.isAssignableFrom(parameterType)) {
			return context.request();
		} else if (HttpServerResponse.class.isAssignableFrom(parameterType)) {
			return context.response();
		} else if (RoutingContext.class.isAssignableFrom(parameterType)) {
			return context;
		} else if (Subject.class.isAssignableFrom(parameterType)) {
			Subject subject = context.get(Constants.SUBJECT_NAME);
			if (subject == null) {
				subject = new SecuritySubject();
				context.put(Constants.SUBJECT_NAME, subject);
			}
			return subject;
		} else if (BindErrors.class.isAssignableFrom(parameterType)) {
			return this.getBindErrors(context);
		} else if (parameter.hasConvertAnnotation()) {
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
	 */
	private Object resolveAnnotation(MethodParameter parameter, RoutingContext context) {
		Body body = parameter.getBodyAnnotation();
		if (body != null) {
			if (!parameter.getParameterType().isArray() && BeanUtils.isSimpleProperty(parameter.getParameterType())) {
				return this.doConvert(context.getBodyAsString(), parameter.getParameterType());
			}
			return context.getBody().getBytes();
		}
		Json json = parameter.getJsonAnnotation();
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
		Header header = parameter.getHeaderAnnotation();
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
	 */
	private Object resolveObjectAndValidate(MethodParameter parameter, RoutingContext context) {

		// 需要验证
		if (validator != null && parameter.getValidAnnotation() != null) {
			return this.resolveObject(parameter, context, true);
		}

		// 不需要验证
		return this.resolveObject(parameter, context, false);
	}

	/**
	 * 初始化绑定错误
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
	 */
	private Object resolveObject(MethodParameter parameter, RoutingContext context, boolean check) {
		Map<String, Object> arguments = this.parseArguments(context.request().params().iterator());
		return this.resolveObject(parameter.getParameterType(), parameter.getParameterName(), arguments, context,
				check);
	}

	/**
	 * 解析参数
	 * 
	 * @param clazz     需转换的类型
	 * @param pname     参数名称
	 * @param arguments 具体的参数
	 * @param context   上下文
	 * @param check     是否校验
	 * @return 解析后的参数
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

	@SuppressWarnings("unchecked")
	private void fillObjectValue(Object obj, Map<String, FieldMeta> fields, String paramName,
			Map<String, Object> arguments, RoutingContext context, boolean check) throws IllegalArgumentException {

		// 优先使用下一级的数据
		Object values = arguments;
		if (arguments.containsKey(paramName)) {
			values = arguments.get(paramName);
		}
		Iterator<Map.Entry<String, Object>> entrys;
		if (!(values instanceof Map)) {
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
						result = this.resolveChildObject(field.getNestedFieldClass(), (Map<String, Object>) value,
								context, check);
					}
				} else if (Map.class.isAssignableFrom(field.getFieldClass())) {
					if (value instanceof Map) {
						result = this.doConvert(value, field.getFieldClass());
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
				String error;
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
	 */
	@SuppressWarnings("unchecked")
	private Object resolveChildObject(Class<?> clazz, Map<String, Object> arguments, RoutingContext context,
			boolean check) {
		List<Object> values = Lists.newArrayList();
		arguments.forEach((key, value) -> {
			if (value instanceof Map) {
				value = this.resolveObject(clazz, StringUtils.EMPTY, (Map<String, Object>) value, context, check);
				values.add(value);
			}
		});
		return values;
	}

	private Map<String, Object> parseHeaders(RoutingContext request) {
		MultiMap maps = request.request().headers();
		Map<String, Object> arguments = new LinkedHashMap<>();
		maps.forEach(entry -> arguments.put(entry.getKey(), entry.getValue()));
		return arguments;
	}

	/**
	 * 解析参数
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
				this.processArgs(k_values, entry, k2, k3, k4);
			}
			// 三层数据设置
			else if (k2 != null && k3 != null) {
				this.processArgs(arguments, entry, key, k2, k3);
			}

			// 两层数据设置
			else if (k2 != null) {
				this.addMap(arguments, entry, key, k2);
			}

			// 一层数据设置, 可以有多个值
			else {
				this.addMap(arguments, key, entry.getValue());
			}
		}
		return arguments;
	}

	@SuppressWarnings("unchecked")
	private void processArgs(Map<String, Object> arguments, Map.Entry<String, String> entry, String key, String k2,
			String k3) {
		Map<String, Object> kValues = (Map<String, Object>) arguments.get(key);
		if (kValues == null) {
			kValues = Maps.newHashMap();
			arguments.put(key, kValues);
		}
		this.addMap(kValues, entry, k2, k3);
	}

	@SuppressWarnings("unchecked")
	private void addMap(Map<String, Object> arguments, Map.Entry<String, String> entry, String key, String k2) {
		Map<String, Object> kValues = (Map<String, Object>) arguments.get(key);
		if (kValues == null) {
			kValues = Maps.newHashMap();
			arguments.put(key, kValues);
		}
		this.addMap(kValues, k2, entry.getValue());
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