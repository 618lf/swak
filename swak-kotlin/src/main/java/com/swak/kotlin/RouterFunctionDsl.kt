package com.swak.kotlin

import com.swak.reactivex.server.HttpServerRequest
import com.swak.reactivex.web.function.*
import io.netty.handler.codec.http.HttpMethod

/**
 *  Example:
 *  @Configuration
 *  open class ApplicationRoutes {
 *
 *   @Bean
 *   fun ShopRouter() = router {
 *        GET("/") {
 *            Observable.fromCallable { 123 }
 *        }
 *   }
 *  }
 */
fun router(routes: RouterFunctionDsl.() -> Unit) = RouterFunctionDsl().apply(routes).router()

open class RouterFunctionDsl {

    private val routes = mutableListOf<RouterFunction>()

    /**
     * Route to the given handler function if the given request predicate applies.
     * @see RouterFunctions.route
     */
    fun POST(pattern: String, f: (HttpServerRequest) -> Any) {
        routes += RouterFunctions.route(RequestPredicates.POST(pattern), HandlerFunction {f(it)})
    }

    /**
     * Route to the given handler function if the given request predicate applies.
     * @see RouterFunctions.route
     */
    fun GET(pattern: String, f: (HttpServerRequest) -> Any) {
        routes += RouterFunctions.route(RequestPredicates.GET(pattern), HandlerFunction {f(it)})
    }

    /**
     * Route to the given handler function if the given path predicate applies.
     * @see RouterFunctions.route
     */
    fun PATH(pattern: String, f: (HttpServerRequest) -> Any) {
        routes += RouterFunctions.route(RequestPredicates.path(pattern), HandlerFunction { f(it) })
    }

    /**
     * Return a {@code RequestPredicate} that tests against the given HTTP method.
     * @param httpMethod the HTTP method to match to
     * @return a predicate that tests against the given HTTP method
     */
    fun method(httpMethod: HttpMethod): RequestPredicate = RequestPredicates.method(httpMethod)

    /**
     * Return a {@code RequestPredicate} that tests the request path against the given path pattern.
     * @see RequestPredicates.path
     */
    fun path(pattern: String): RequestPredicate = RequestPredicates.path(pattern)

    /**
     * Return a composed routing function created from all the registered routes.
     */
    internal fun router(): RouterFunction {
        return routes.reduce(RouterFunction::and)
    }
}