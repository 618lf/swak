package com.swak.reactivex.web.function;

import com.swak.reactivex.server.HttpServerRequest;

/**
 * 构建 RouterFunction 的入口
 * 
 * @author lifeng
 */
public abstract class RouterFunctions {

	/**
	 * 实现一些公用的方法
	 * 
	 * @author lifeng
	 */
	public static abstract class AbstractRouterFunction implements RouterFunction {

	}
	
	/**
	 * 
	 * @author lifeng
	 */
	public static class ComposedRouterFunction extends AbstractRouterFunction {
		private final RouterFunction first;
		private final RouterFunction second;
		public ComposedRouterFunction(RouterFunction first, RouterFunction second) {
			this.first = first;
			this.second = second;
		}
		
		@Override
		public HandlerFunction route(HttpServerRequest request) {
			HandlerFunction hf = first.route(request);
			return hf == null? second.route(request) : hf;
		}
	}
	
	/**
	 * 默认的 Router Function
	 * 
	 * @author lifeng
	 */
	public static class DefaultRouterFunction extends AbstractRouterFunction {

		private final RequestPredicate predicate;
		private final HandlerFunction handlerFunction;

		public DefaultRouterFunction(RequestPredicate predicate, HandlerFunction handlerFunction) {
			this.predicate = predicate;
			this.handlerFunction = handlerFunction;
		}

		/**
		 * 获得 HandlerFunction
		 */
		@Override
		public HandlerFunction route(HttpServerRequest request) {
			if (this.predicate.test(request)) {
				return this.handlerFunction;
			}
			return null;
		}
	}
}
