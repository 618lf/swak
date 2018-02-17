package com.swak.rpc.config;

import java.lang.reflect.Method;

import com.swak.rpc.annotation.RpcService;

public class MethodConfig {

	/** 服务方法 */
	public final Method method;
	
	/** 版本 */
	public final String version;
	
	/** millseconds */
	public final long timeout;
	
	/** 忽略 */
	public final boolean ignore;
	
	/** rest路径 */
	public final String rest;
	
	/**
	 * @param method
	 *            服务方法
	 */
	public MethodConfig(Method method) {
		this.method = method;

		this.version = version(method);
		this.timeout = timeout(method);
		this.ignore = ignore(method);
		this.rest = rest(method);
	}

	public MethodConfig(Method method, String version, long timeout, boolean ignore, String rest) {
		this.method = method;
		this.version = version;
		this.timeout = timeout;
		this.ignore = ignore;
		this.rest = rest;
	}

	private String version(Method method) {
		String version = RpcService.DEFAULT_VERSION;

		RpcService config = method.getDeclaringClass().getAnnotation(RpcService.class);
		if (config == null) {
			config = method.getAnnotation(RpcService.class);
		}

		if (config != null) {
			version = config.version();
		}

		int delimterIndex = version.indexOf('.');
		if (delimterIndex > 0) {
			version = version.substring(0, delimterIndex);
		}

		return version;
	}

	private long timeout(Method method) {
		long timeout = RpcService.DEFAULT_TIME_OUT;

		RpcService config = method.getDeclaringClass().getAnnotation(RpcService.class);
		if (config == null) {
			config = method.getAnnotation(RpcService.class);
		}

		if (config != null) {
			timeout = config.timeout();
		}

		if (timeout < 1) {
			timeout = RpcService.DEFAULT_TIME_OUT;
		}

		return timeout;
	}

	private boolean ignore(Method method) {
		boolean ignore = RpcService.DEFAULT_IGNORE;

		RpcService config = method.getDeclaringClass().getAnnotation(RpcService.class);

		if (config != null) {
			ignore = config.ignore();
		}

		if (!ignore) {
			config = method.getAnnotation(RpcService.class);

			if (config != null) {
				ignore = config.ignore();
			}
		}

		return ignore;
	}

	private String rest(Method method) {
		return "";
	}

	@Override
	public String toString() {
		return "RemoteMethodConfig{" + //
				"method=" + method + //
				", version='" + version + '\'' + //
				", timeout=" + timeout + //
				", ignore=" + ignore + //
				", rest='" + rest + '\'' + //
				'}';
	}
}
