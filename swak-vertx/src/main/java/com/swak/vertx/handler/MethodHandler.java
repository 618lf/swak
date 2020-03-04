package com.swak.vertx.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.util.ClassUtils;

import com.swak.annotation.Logical;
import com.swak.annotation.RequiresPermissions;
import com.swak.annotation.RequiresRoles;
import com.swak.annotation.Sync;
import com.swak.meters.MethodMetrics;
import com.swak.meters.MetricsFactory;
import com.swak.security.Permission;
import com.swak.security.permission.AndPermission;
import com.swak.security.permission.OrPermission;
import com.swak.security.permission.SinglePermission;
import com.swak.utils.ReflectUtils;

/**
 * 基于 method 的执行器
 * 
 * @author lifeng
 */
@SuppressWarnings("rawtypes")
public class MethodHandler {

	// 操作符
	final static byte operators_sync = 1 << 0; // 同步操作

	private final Object bean;
	private final Class<?> beanType;
	private final String name;
	private final Method method;
	private final MethodParameter[] parameters;
	private volatile Annotation[] annotations;
	protected MethodMetrics metrics;
	protected Permission requiresRoles;
	protected Permission requiresPermissions;
	protected byte operators = 0;

	/**
	 * Create an instance from a bean instance and a method.
	 */
	public MethodHandler(Object bean, Method method) {
		this.bean = bean;
		this.beanType = ClassUtils.getUserClass(bean);
		this.method = method;
		this.name = new StringBuilder(this.beanType.getName()).append(".")
				.append(ReflectUtils.getMethodDesc(this.method)).toString();
		this.parameters = initMethodParameters();
		this.initOperators();
	}

	private MethodParameter[] initMethodParameters() {
		int count = this.method.getParameterTypes().length;
		MethodParameter[] result = new MethodParameter[count];
		for (int i = 0; i < count; i++) {
			result[i] = new MethodParameter(this.beanType, this.method, i);
		}
		return result;
	}

	private void initOperators() {
		Sync sync = (Sync) this.getAnnotation(Sync.class);
		if (sync != null) {
			operators |= operators_sync;
		}
	}

	public Object getBean() {
		return bean;
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getBeanType() {
		return beanType;
	}

	public MethodParameter[] getParameters() {
		return parameters;
	}

	public Annotation[] getAnnotations() {
		if (this.annotations == null) {
			this.annotations = this.method.getAnnotations();
		}
		return this.annotations;
	}

	public <A extends Annotation> Annotation getAnnotation(Class<A> annotationType) {
		Annotation[] paramAnns = this.getAnnotations();
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

	public <A extends Annotation> boolean hasAnnotation(Class<A> annotationType) {
		Annotation[] paramAnns = this.getAnnotations();
		if (paramAnns != null) {
			for (Annotation ann : paramAnns) {
				if (annotationType.isInstance(ann)) {
					return true;
				}
			}
			return false;
		}
		return false;
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

	/**
	 * 权限
	 */
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

	/**
	 * 是否同步操作
	 */
	public boolean isSync() {
		return (operators & operators_sync) > 0;
	}

	/**
	 * 设置监控
	 * 
	 * @param metricsFactory
	 * @return
	 */
	public MethodHandler applyMetrics(MetricsFactory metricsFactory) {
		if (metricsFactory != null) {
			metrics = metricsFactory.createMethodMetrics(this.method, name);
		}
		return this;
	}

	/**
	 * 调用
	 * 
	 * @param args
	 * @return
	 */
	public Object doInvoke(Object[] args) throws Exception {
		return this.getMethod().invoke(this.getBean(), args);
	}
}