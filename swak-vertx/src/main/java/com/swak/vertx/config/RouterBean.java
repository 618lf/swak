package com.swak.vertx.config;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.swak.vertx.annotation.RequestMethod;
import com.swak.vertx.handler.MethodHandler;

/**
 * 路由 bean
 * 
 * @author lifeng
 */
public class RouterBean {

	private final Set<String> patterns;
	private final RequestMethod requestMethod;
	private final MethodHandler methodHandler;

	public RouterBean(Object bean, Method method, List<String> patterns, RequestMethod requestMethod) {
		this.patterns = prependLeadingSlash(patterns);
		this.requestMethod = requestMethod;
		this.methodHandler = new MethodHandler(bean, method);
	}

	private Set<String> prependLeadingSlash(Collection<String> patterns) {
		if (patterns == null) {
			return Collections.emptySet();
		}
		Set<String> result = new LinkedHashSet<String>(patterns.size());
		for (String pattern : patterns) {
			if (StringUtils.hasLength(pattern) && !pattern.startsWith("/")) {
				pattern = new StringBuilder("/").append(pattern).toString();
			}
			result.add(pattern);
		}
		return result;
	}

	public Set<String> getPatterns() {
		return patterns;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}
	public MethodHandler getHandler() {
		return methodHandler;
	}
}
