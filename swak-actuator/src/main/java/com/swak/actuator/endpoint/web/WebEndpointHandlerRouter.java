package com.swak.actuator.endpoint.web;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import com.swak.Constants;
import com.swak.actuator.endpoint.InvocationContext;
import com.swak.vertx.annotation.RouterSupplier;
import com.swak.vertx.config.IRouterSupplier;
import com.swak.vertx.handler.HandlerAdapter;
import com.swak.vertx.handler.MethodHandler;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Router 提供器
 * 
 * @author lifeng
 */
@RouterSupplier
public class WebEndpointHandlerRouter implements IRouterSupplier {

	final WebEndpointsSupplier webEndpointsSupplier;
	final HandlerAdapter handlerAdapter;

	public WebEndpointHandlerRouter(HandlerAdapter handlerAdapter, WebEndpointsSupplier webEndpointsSupplier) {
		this.webEndpointsSupplier = webEndpointsSupplier;
		this.handlerAdapter = handlerAdapter;
	}

	@Override
	public Router get(Vertx vertx) {
		try {
			Router router = initRouter(Router.router(vertx));
			return router;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String path() {
		return webEndpointsSupplier.getRootPath();
	}

	private Router initRouter(Router router) throws NoSuchMethodException, SecurityException {
		Method method = WebMvcOperationAdapter.class.getMethod("handle", RoutingContext.class);
		for (ExposableWebEndpoint endpoint : this.webEndpointsSupplier.getEndpoints()) {
			for (WebOperation operation : endpoint.getOperations()) {
				WebMvcOperationAdapter adapter = new WebMvcOperationAdapter(operation);
				MethodHandler methodHandler = new MethodHandler(adapter, method);
				router.get(adapter.getPath()).handler(context -> {
					handlerAdapter.handle(context, methodHandler);
				});
			}
		}
		return router;
	}

	// ----------- 将endpoint 转为 controller ------------
	@FunctionalInterface
	public interface WebMvcOperation {
		Object handle(RoutingContext request);
	}

	public class WebMvcOperationAdapter implements WebMvcOperation {

		private final WebOperation operation;

		public WebMvcOperationAdapter(WebOperation operation) {
			this.operation = operation;
		}

		@Override
		public Object handle(RoutingContext request) {
			Map<String, Object> arguments = getArguments(request);
			return this.operation.invoke(new InvocationContext(request, arguments));
		}

		private Map<String, Object> getArguments(RoutingContext request) {
			MultiMap maps = request.request().params();
			Map<String, Object> arguments = new LinkedHashMap<>();
			maps.forEach(entry -> {
				arguments.put(entry.getKey(), entry.getValue());
			});
			return arguments;
		}

		public String getPath() {
			return new StringBuilder().append(Constants.URL_PATH_SEPARATE).append(operation.getId()).toString();
		}

		@Override
		public String toString() {
			return operation.toString();
		}
	}
}
