package com.swak.vertx.transport;

import java.util.Set;

import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.RequestMethod;
import com.swak.vertx.config.IRouterConfig;
import com.swak.vertx.config.IRouterSupplier;
import com.swak.vertx.config.RouterBean;
import com.swak.vertx.handler.MethodHandler;
import com.swak.vertx.handler.RouterHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * http服务器
 * 
 * @author lifeng
 */
public class HttpVerticle extends AbstractVerticle {

	// 系统唯一的 router
	private static Router router;

	// 通用的配置
	private Set<RouterBean> routers;
	private Set<IRouterConfig> routerConfigs;
	private Set<IRouterSupplier> routerSuppliers;
	private int port;
	private final RouterHandler routerHandler;

	public HttpVerticle(Set<IRouterConfig> routerConfigs, Set<RouterBean> routers, Set<IRouterSupplier> routerSuppliers, RouterHandler routerHandler, int port) {
		this.routerConfigs = routerConfigs;
		this.routers = routers;
		this.routerSuppliers = routerSuppliers;
		this.routerHandler = routerHandler;
		this.port = port;
	}

	private synchronized Router initRouter() {
		if (router == null) {
			router = Router.router(vertx);
			
			// 路由基本配置
			for(IRouterConfig config: routerConfigs) {
				config.apply(router);
			}
			
			// 单个路由定义
			for (RouterBean rb : routers) {
				Set<String> paths = rb.getPatterns();
				for (String path : paths) {
					Route route = null;
					if (StringUtils.contains(path, "*")) {
						route = router.patchWithRegex(path);
					} else {
						route = router.patch(path);
					}
					if (rb.getRequestMethod() == RequestMethod.POST) {
						route.method(HttpMethod.POST);
					} else {
						route.method(HttpMethod.GET);
					}
					
					// 具体的处理器
					MethodHandler handler = rb.getHandler();
					
					// 初始化处理器
					routerHandler.initHandler(handler);

					// 绑定处理器
					route.handler(ctx -> {
						routerHandler.handle(ctx, handler);
					});
				}
			}
			
			// 路由组定义
			for (IRouterSupplier rs : routerSuppliers) {
				Router router = rs.get(vertx);
				if (router != null) {
					router.mountSubRouter(rs.path(), router);
				}
			}
		}
		return router;
	}
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		// 初始化 router
		Router router = this.initRouter();
		
		// 发布服务
		vertx.createHttpServer().requestHandler(router::accept).listen(port, res -> {
			startFuture.complete();
		});
	}
}