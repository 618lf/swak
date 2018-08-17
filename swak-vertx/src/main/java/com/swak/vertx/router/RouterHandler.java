package com.swak.vertx.router;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.swak.vertx.annotation.RequestMethod;

import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 * 
 * @author lifeng
 * @param <T>
 */
public class RouterHandler extends AbstractRouterHandler {

	private MethodHandler handler;
	private RequestMappingRouterAdapter handlerAdapter;

	public RouterHandler(List<String> patterns, RequestMethod method) {
		super(patterns, method);
	}
	
	public void setHandler(MethodHandler handler) {
		this.handler = handler;
	}

	public void setHandlerAdapter(RequestMappingRouterAdapter handlerAdapter) {
		this.handlerAdapter = handlerAdapter;
	}

	/**
	 * 合并
	 * @param other
	 * @return
	 */
	public RouterHandler combine(RouterHandler other) {

		// patterns
		List<String> result = new LinkedList<String>();
		if (!this.getPatterns().isEmpty() && !other.getPatterns().isEmpty()) {
			for (String pattern1 : this.getPatterns()) {
				for (String pattern2 : other.getPatterns()) {
					result.add(this.combine(pattern1, pattern2));
				}
			}
		} else if (!this.getPatterns().isEmpty()) {
			result.addAll(this.getPatterns());
		} else if (!other.getPatterns().isEmpty()) {
			result.addAll(other.getPatterns());
		} else {
			result.add("");
		}

		// method
		RequestMethod method = other.getMethod() == null ? this.getMethod() : other.getMethod();

		return new RouterHandler(result, method);
	}

	/**
	 * 提供标准的处理方式
	 */
	@Override
	public void handle(RoutingContext context) {
		handlerAdapter.handle(context, handler);
	}
	
	/**
	 * 构建
	 * 
	 * @param paths
	 * @return
	 */
	public static RouterHandler paths(RequestMethod method, String... paths) {
		return new RouterHandler(Arrays.asList(paths), method);
	}
}