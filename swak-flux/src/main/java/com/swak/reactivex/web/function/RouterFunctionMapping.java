package com.swak.reactivex.web.function;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.web.Handler;
import com.swak.reactivex.web.HandlerMapping;
import com.swak.utils.Lists;

public class RouterFunctionMapping implements HandlerMapping, ApplicationContextAware, Ordered {

	@Nullable
	private RouterFunction routerFunction;

	@Override
	public void close() throws IOException {
		routerFunction = null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.initRouterFunctions(applicationContext);
	}

	private void initRouterFunctions(ApplicationContext applicationContext) {
		List<RouterFunction> functions = Lists.newArrayList();
		String[] names = applicationContext.getBeanNamesForType(RouterFunction.class);
		for (String name : names) {
			RouterFunction rf = applicationContext.getBean(name, RouterFunction.class);
			functions.add(rf);
		}
		this.routerFunction = functions.stream().reduce(RouterFunction::and).orElse(null);
	}
	
	/**
	 * 获取handler
	 */
	@Override
	public Handler getHandler(HttpServerRequest request) {
		if (this.routerFunction != null) {
			return this.routerFunction.route(request);
		}
		return null;
	}
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 10;
	}

	public RouterFunction getRouterFunction() {
		return routerFunction;
	}
}