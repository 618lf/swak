package com.swak.vertx.dsl

import com.swak.vertx.dsl.RouterFunction
import com.swak.vertx.dsl.RouterFunctions
import io.vertx.core.Handler
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

/**
 * Dsl 定义
 */
public fun router(router: Router, routes: RouterFunctionDsl.() -> Unit) =
	RouterFunctionDsl().apply(routes).router(router);

/**
 * 定义
 */
open class RouterFunctionDsl {

	private val routes = mutableListOf<RouterFunction>()

	/**
	 * Route to the given handler function if the given request predicate applies.
	 * @see RouterFunctions.Post
	 */
	fun POST(path: String, f: (RoutingContext) -> Any) {
		routes += RouterFunctions.Post(path, Handler { f(it) })
	}

	/**
	 * Route to the given handler function if the given request predicate applies.
	 * @see RouterFunctions.Get
	 */
	fun GET(path: String, f: (RoutingContext) -> Any) {
		routes += RouterFunctions.Get(path, Handler { f(it) })
	}

	/**
	 * Route to the given handler function if the given request predicate applies.
	 * @see RouterFunctions.Get
	 */
	fun PATH(path: String, f: (RoutingContext) -> Any) {
		routes += RouterFunctions.Path(path, Handler { f(it) })
	}

	/**
	 * Return a composed routing function created from all the registered routes.
	 */
	internal fun router(router: Router) {
		routes.map { route -> route.route(router) }
	}
}