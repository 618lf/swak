package com.swak.vertx.config;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.swak.annotation.RequestMethod;
import com.swak.utils.StringUtils;
import com.swak.vertx.protocol.http.FluxMethodInvoker;
import com.swak.vertx.protocol.http.MethodInvoker;
import com.swak.vertx.protocol.http.RouterHandler;
import com.swak.vertx.transport.VertxProxy;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.EqualsAndHashCode;

/**
 * 路由 bean
 *
 * @author: lifeng
 * @date: 2020/3/29 18:55
 */
@EqualsAndHashCode
public class RouterBean implements Handler<RoutingContext> {

	private final Set<String> patterns;
	private final RequestMethod requestMethod;
	private final MethodInvoker methodInvoker;
	private RouterHandler handlerAdapter;

	public RouterBean(VertxProxy vertx, Class<?> clazz, Object bean, Method method, List<String> patterns,
			RequestMethod requestMethod, boolean mergeService) {
		this.patterns = this.prependLeadingSlash(patterns);
		this.requestMethod = requestMethod;
		this.methodInvoker = this.prependMethodHandler(vertx, clazz, bean, method, mergeService);
	}

	/**
	 * 处理请求地址
	 *
	 * @param patterns
	 * @return
	 */
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

	/**
	 * 处理请求的执行的方法
	 *
	 * @param mergeService
	 * @return
	 */
	private MethodInvoker prependMethodHandler(VertxProxy vertx, Class<?> clazz, Object bean, Method method, boolean mergeService) {
		return mergeService ? new FluxMethodInvoker(vertx, clazz, bean, method) : new MethodInvoker(clazz, bean, method);
	}

	/**
	 * 设置适配器
	 *
	 * @param handlerAdapter
	 * @return
	 */
	public RouterBean adapter(RouterHandler handlerAdapter) {
		this.handlerAdapter = handlerAdapter;
		this.handlerAdapter.initHandler(methodInvoker);
		return this;
	}

	/**
	 * 挂在到路由
	 *
	 * @param router
	 * @return
	 */
	public RouterBean mounton(Router router) {
		Set<String> paths = this.patterns;
		for (String path : paths) {
			Route route = null;
			if (StringUtils.contains(path, "*")) {
				route = router.routeWithRegex(path);
			} else {
				route = router.route(path);
			}
			if (this.requestMethod == RequestMethod.POST) {
				route.method(HttpMethod.POST);
			} else if (this.requestMethod == RequestMethod.GET) {
				route.method(HttpMethod.GET);
			}
			route.handler(this);
		}
		return this;
	}

	/**
	 * 执行请求
	 */
	@Override
	public void handle(RoutingContext context) {
		this.handlerAdapter.handle(context, methodInvoker);
	}
}
