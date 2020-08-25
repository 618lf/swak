package com.swak.vertx.config;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.swak.annotation.FluxService;
import com.swak.annotation.RequestMapping;
import com.swak.annotation.RequestMethod;
import com.swak.annotation.RestApi;
import com.swak.meters.MetricsFactory;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.utils.router.RouterUtils;
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
public class RouterBean implements Handler<RoutingContext>, InitializingBean, AbstractConfig {

	@Autowired
	private VertxProxy proxy;
	@Autowired
	private RouterHandler handlerAdapter;
	@Autowired(required = false)
	private MetricsFactory metricsFactory;
	private Set<String> patterns;
	private RequestMethod requestMethod;
	private MethodInvoker methodInvoker;
	private Class<?> type;
	private Object ref;
	private Method method;
	private int port;

	@Override
	public void afterPropertiesSet() throws Exception {
		RestApi apiMapping = AnnotatedElementUtils.findMergedAnnotation(type, RestApi.class);
		FluxService serviceMapping = AnnotatedElementUtils.findMergedAnnotation(type, FluxService.class);
		RequestMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(type, RequestMapping.class);
		RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);

		String[] patterns1 = classMapping.value();
		String[] patterns2 = methodMapping.value();
		List<String> result = Lists.newArrayList();
		if (patterns1.length != 0 && patterns2.length != 0) {
			for (String pattern1 : patterns1) {
				for (String pattern2 : patterns2) {
					result.add(RouterUtils.combine(pattern1, pattern2));
				}
			}
		} else if (patterns1.length != 0) {
			result = Lists.newArrayList(patterns1);
		} else if (patterns2.length != 0) {
			result = Lists.newArrayList(patterns2);
		} else {
			result.add(StringUtils.EMPTY);
		}

		// 端口
		this.port = apiMapping.port();

		// patterns
		this.patterns = this.prependLeadingSlash(result);

		// method
		RequestMethod requestMethod = classMapping.method() == RequestMethod.ALL ? methodMapping.method()
				: classMapping.method();
		this.requestMethod = requestMethod == RequestMethod.ALL ? null : requestMethod;
		this.methodInvoker = this.prependMethodHandler(proxy, type, ref, method, serviceMapping != null);

		// 应用监控
		this.methodInvoker.applyMetrics(metricsFactory);
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
	private MethodInvoker prependMethodHandler(VertxProxy vertx, Class<?> clazz, Object bean, Method method,
			boolean mergeService) {
		return mergeService ? new FluxMethodInvoker(vertx, clazz, bean, method)
				: new MethodInvoker(clazz, bean, method);
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

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public RouterHandler getHandlerAdapter() {
		return handlerAdapter;
	}
}
