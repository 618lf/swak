package com.swak.vertx.protocol.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.swak.annotation.Body;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.annotation.Logical;
import com.swak.annotation.RequiresPermissions;
import com.swak.annotation.RequiresRoles;
import com.swak.annotation.Valid;
import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.asm.Wrapper;
import com.swak.meters.MethodMetrics;
import com.swak.meters.MetricsFactory;
import com.swak.security.Permission;
import com.swak.security.permission.AndPermission;
import com.swak.security.permission.OrPermission;
import com.swak.security.permission.SinglePermission;
import com.swak.utils.router.RouterUtils;

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
	protected MethodMetrics metrics;
	protected String metricName;
	protected Permission requiresRoles;
	protected Permission requiresPermissions;

	/**
	 * 代理此类提高执行效率
	 */
	protected Wrapper wrapper;
	protected MethodMeta methodMeta;

	public MethodInvoker(Class<?> clazz, Object bean, Method method) {
		this.bean = bean;
		this.method = method;
		this.wrapper = Wrapper.getWrapper(clazz);
		this.methodMeta = MethodCache.get(clazz).lookup(this.method);
		this.metricName = this.bean.getClass().getName() + "." + this.methodMeta.getMethodDesc();
		this.parameters = this.initMethodParameters();
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
	public void applyMetrics(MetricsFactory metricsFactory) {
		if (metricsFactory != null) {
			metrics = metricsFactory.createMethodMetrics(this.method, this.metricName);
		}
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