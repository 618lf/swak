package com.swak.flux.web.function;

import org.springframework.util.Assert;

import com.swak.flux.transport.server.HttpServerRequest;

/**
 * 构建 RouterFunction 的入口
 * 
 * @author lifeng
 */
public abstract class RouterFunctions {

	/**
	 * Route to the given handler function if the given request predicate applies.
	 * <p>For instance, the following example routes GET requests for "/user" to the
	 * {@code listUsers} method in {@code userController}:
	 * <pre class="code">
	 * RouterFunction&lt;ServerResponse&gt; route =
	 *     RouterFunctions.route(RequestPredicates.GET("/user"), userController::listUsers);
	 * </pre>
	 * @param predicate the predicate to test
	 * @param handlerFunction the handler function to route to if the predicate applies
	 * @param <T> the type of response returned by the handler function
	 * @return a router function that routes to {@code handlerFunction} if
	 * {@code predicate} evaluates to {@code true}
	 * @see RequestPredicates
	 */
	public static RouterFunction route(RequestPredicate predicate,
			HandlerFunction handlerFunction) {
		Assert.notNull(predicate, "'predicate' must not be null");
		Assert.notNull(handlerFunction, "'handlerFunction' must not be null");
		return new DefaultRouterFunction(predicate, handlerFunction);
	}
	
	/**
	 * 实现一些公用的方法
	 * 
	 * @author lifeng
	 */
	private static abstract class AbstractRouterFunction implements RouterFunction {}
	
	/**
	 * 
	 * @author lifeng
	 */
	public static class ComposedRouterFunction extends AbstractRouterFunction {
		private final RouterFunction first;
		private final RouterFunction second;
		ComposedRouterFunction(RouterFunction first, RouterFunction second) {
			this.first = first;
			this.second = second;
		}
		
		@Override
		public HandlerFunction route(HttpServerRequest request) {
			HandlerFunction hf = first.route(request);
			return hf == null? second.route(request) : hf;
		}
		
		/**
		 * 左树
		 * @return
		 */
		public RouterFunction getFirst() {
			return first;
		}

		/**
		 * 右树
		 * @return
		 */
		public RouterFunction getSecond() {
			return second;
		}

		@Override
		public String toString() {
			return String.format("(left %s or right %s)", first, second);
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

		DefaultRouterFunction(RequestPredicate predicate, HandlerFunction handlerFunction) {
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

		/**
		 * 条件
		 * @return
		 */
		public RequestPredicate getPredicate() {
			return predicate;
		}
		
		@Override
		public String toString() {
			return String.format("(predicate %s -> function %s)", predicate.toString(), handlerFunction.description());
		}
	}
}
