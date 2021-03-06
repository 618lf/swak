package com.swak.vertx.invoker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.swak.annotation.Body;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.annotation.Logical;
import com.swak.annotation.RequiresPermissions;
import com.swak.annotation.RequiresRoles;
import com.swak.annotation.Valid;
import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;
import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.asm.Wrapper;
import com.swak.meters.MethodMetrics;
import com.swak.meters.MetricsFactory;
import com.swak.security.Permission;
import com.swak.security.Subject;
import com.swak.security.permission.AndPermission;
import com.swak.security.permission.OrPermission;
import com.swak.security.permission.SinglePermission;
import com.swak.utils.router.RouterUtils;
import com.swak.validator.errors.BindErrors;
import com.swak.vertx.protocol.im.ImContext;
import com.swak.vertx.protocol.im.ImContext.ImRequest;
import com.swak.vertx.protocol.im.ImContext.ImResponse;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * 基于 method 的执行器
 *
 * @author: lifeng
 * @date: 2020/3/29 20:14
 */
@SuppressWarnings("rawtypes")
public class MethodInvoker implements HandlerInvoker {

	private final Object bean;
	private final Method method;
	private final MethodParameter[] parameters;
	protected Permission requiresRoles;
	protected Permission requiresPermissions;

	/**
	 * 代理此类提高执行效率
	 */
	protected Wrapper wrapper;
	protected MethodMeta methodMeta;

	/**
	 * 使用指标统计
	 */
	public MethodMetrics metrics;

	public MethodInvoker(Class<?> clazz, Object bean, Method method) {
		this.bean = bean;
		this.method = method;
		this.wrapper = Wrapper.getWrapper(clazz);
		this.methodMeta = MethodCache.get(clazz).lookup(this.method);
		this.parameters = this.initMethodParameters();
		this.initCache();
	}

	private MethodParameter[] initMethodParameters() {
		Class<?>[] parameterTypes = this.methodMeta.getParameterTypes();
		Class<?>[] nestedParameterTypes = this.methodMeta.getNestedParameterTypes();
		String[] parameterNames = RouterUtils.getParameterNames(method);
		MethodParameter[] result = new MethodParameter[parameterTypes.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = new MethodParameter(this.method, parameterNames[i], i, parameterTypes[i],
					nestedParameterTypes[i]);
		}
		return result;
	}

	private void initCache() {
		MethodParameter[] parameters = this.getParameters();
		for (MethodParameter parameter : parameters) {
			// 实际的类型
			Class<?> parameterType = parameter.getNestedParameterType();

			// 对于集合类型支持第一层
			this.initField(parameterType);
		}
	}

	/**
	 * 组装类型，子类型也一并组装
	 */
	private void initField(Class<?> parameterType) {
		if (parameterType == null || HttpServerRequest.class.isAssignableFrom(parameterType)
				|| HttpServerResponse.class.isAssignableFrom(parameterType)
				|| RoutingContext.class.isAssignableFrom(parameterType)
				|| ImRequest.class.isAssignableFrom(parameterType) || ImResponse.class.isAssignableFrom(parameterType)
				|| ImContext.class.isAssignableFrom(parameterType) || Subject.class.isAssignableFrom(parameterType)
				|| BindErrors.class.isAssignableFrom(parameterType) || BeanUtils.isSimpleProperty(parameterType)
				|| Collection.class.isAssignableFrom(parameterType) || List.class.isAssignableFrom(parameterType)
				|| Map.class.isAssignableFrom(parameterType)) {
			return;
		}

		// 不存在的类型需要去解析, 防止死循环
		if (!FieldCache.exists(parameterType)) {
			// 缓存父类型
			FieldCache.set(parameterType);

			// 子类型
			ClassMeta classMeta = FieldCache.get(parameterType);
			classMeta.getFields().values().forEach(field -> this.initField(field.getNestedFieldClass()));
		}
	}

	public MethodParameter[] getParameters() {
		return parameters;
	}

	/**
	 * 角色
	 */
	public Permission getRequiresRoles() {
		if (this.requiresRoles == null) {
			RequiresRoles roles = (RequiresRoles) this.getAnnotation(RequiresRoles.class);
			if (roles == null || roles.value().length == 0) {
				this.requiresRoles = Permission.NONE;
			} else if (roles.value().length == 1) {
				this.requiresRoles = new SinglePermission(roles.value()[0]);
			} else if (roles.logical() == Logical.AND) {
				this.requiresRoles = new AndPermission(roles.value());
			} else if (roles.logical() == Logical.OR) {
				this.requiresRoles = new OrPermission(roles.value());
			}
		}
		return this.requiresRoles;
	}

	public Permission getRequiresPermissions() {
		if (this.requiresPermissions == null) {
			RequiresPermissions roles = (RequiresPermissions) this.getAnnotation(RequiresPermissions.class);
			if (roles == null || roles.value().length == 0) {
				this.requiresPermissions = Permission.NONE;
			} else if (roles.value().length == 1) {
				this.requiresPermissions = new SinglePermission(roles.value()[0]);
			} else if (roles.logical() == Logical.AND) {
				this.requiresPermissions = new AndPermission(roles.value());
			} else if (roles.logical() == Logical.OR) {
				this.requiresPermissions = new OrPermission(roles.value());
			}
		}
		return this.requiresPermissions;
	}

	private <A extends Annotation> Annotation getAnnotation(Class<A> annotationType) {
		Annotation[] paramAnns = this.method.getAnnotations();
		if (paramAnns != null) {
			for (Annotation ann : paramAnns) {
				if (annotationType.isInstance(ann)) {
					return ann;
				}
			}
			return null;
		}
		return null;
	}

	@Override
	public MethodMetrics applyMetrics(MetricsFactory metricsFactory) {
		String metricName = this.bean.getClass().getName() + "." + this.methodMeta.getMethodDesc();
		this.metrics = this.methodMeta.applyMetrics(metricsFactory, metricName);
		return this.metrics;
	}

	@Override
	public Object doInvoke(Object[] args) throws Throwable {
		return this.wrapper.invokeMethod(this.bean, this.methodMeta.getMethodDesc(), args);
	}

	/**
	 * 不支持 Map 的
	 * 
	 * @author lifeng
	 * @date 2020年4月22日 下午8:36:29
	 */
	public class MethodParameter {

		private Method method;
		private int parameterIndex;
		private String parameterName;
		private Class<?> parameterType;
		private Class<?> nestedParameterType;
		private boolean hasInitAnnotation;

		private Body body;
		private Json json;
		private Header header;
		private Valid valid;

		public MethodParameter(Method method, String parameterName, int parameterIndex, Class<?> parameterType,
				Class<?> nestedParameterType) {
			this.method = method;
			this.parameterIndex = parameterIndex;
			this.parameterType = parameterType;
			this.nestedParameterType = nestedParameterType;
			this.parameterName = parameterName;
		}

		public String getParameterName() {
			return parameterName;
		}

		public Class<?> getParameterType() {
			return parameterType;
		}

		public Class<?> getNestedParameterType() {
			return nestedParameterType;
		}

		private void initAnnotations() {
			if (!this.hasInitAnnotation) {
				Annotation[][] annotationArray = this.method.getParameterAnnotations();
				Annotation[] paramAnns = annotationArray[this.parameterIndex];
				if (paramAnns != null) {
					for (Annotation ann : paramAnns) {
						if (Body.class.isInstance(ann)) {
							body = (Body) ann;
						} else if (Json.class.isInstance(ann)) {
							json = (Json) ann;
						} else if (Header.class.isInstance(ann)) {
							header = (Header) ann;
						} else if (Valid.class.isInstance(ann)) {
							valid = (Valid) ann;
						}
					}
				}
				this.hasInitAnnotation = true;
			}
		}

		public Method getMethod() {
			return method;
		}

		public int getParameterIndex() {
			return parameterIndex;
		}

		public boolean hasConvertAnnotation() {
			this.initAnnotations();
			return this.body != null || this.json != null || this.header != null;
		}

		public Body getBodyAnnotation() {
			return body;
		}

		public Json getJsonAnnotation() {
			return json;
		}

		public Header getHeaderAnnotation() {
			return header;
		}

		public Valid getValidAnnotation() {
			return valid;
		}
	}
}