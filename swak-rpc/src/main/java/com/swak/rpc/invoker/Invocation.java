package com.swak.rpc.invoker;

import java.lang.reflect.Method;

import com.swak.rpc.annotation.RpcService;
import com.swak.utils.StringUtils;

/**
 * 查找 method 的 条件
 * 
 * @author lifeng
 */
public class Invocation {

	// 基本信息
	private Class<?> serviceType;
	private Object service;
	private Method method;
	private Class<?>[] parameterTypes;
	private String version = null;
	private String group = null;
	
	// 控制
	private long timeout = -1;
	private boolean ignore = false;
	
	public Class<?> getServiceType() {
		return serviceType;
	}

	public void setServiceType(Class<?> serviceType) {
		this.serviceType = serviceType;
	}

	public Object getService() {
		return service;
	}

	public void setService(Object service) {
		this.service = service;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public boolean isIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	/**
	 * 合并
	 */
	public Invocation combine(Invocation other) {
		String version = other.getVersion() == null ? this.getVersion() : other.getVersion();
		long timeout = other.getTimeout() == -1 ? this.getTimeout() : other.getTimeout();
		boolean ignore = other.isIgnore() ? other.isIgnore() : this.isIgnore();
		return Invocation.build(version, timeout, ignore);
	}
	
	/**
	 * 固化，之后不能在改变
	 * @return
	 */
	public Invocation fixed(Class<?> serviceType, Object service, Method method) {
		this.serviceType = serviceType;
		this.service = service;
		this.method = method;
		this.parameterTypes = method.getParameterTypes();
		if (!StringUtils.hasText(version)) {
			version = RpcService.DEFAULT_VERSION;
		}
		if (timeout  == -1) {
			timeout = RpcService.DEFAULT_TIME_OUT;
		}
		// 返回
		return this;
	}
	
	// ----------- 创建 Info ----------------
	public static Invocation build(String version) {
		Invocation info = new Invocation();
		info.setVersion(version);
		return info;
	}
	public static Invocation build(String version, long timeout, boolean ignore) {
		Invocation info = new Invocation();
		info.setVersion(version);
		info.setTimeout(timeout);
		info.setIgnore(ignore);
		return info;
	}
}