package com.swak.vertx.handler;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.swak.exception.BaseRuntimeException;
import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.RequestMethod;
import com.swak.vertx.config.AnnotationBean;
import com.swak.vertx.config.IRouterConfig;
import com.swak.vertx.config.IRouterSupplier;
import com.swak.vertx.config.RouterBean;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * 实现一些默认的方法
 * 
 * @author lifeng
 */
public abstract class AbstractRouterHandler implements RouterHandler {

	// 系统唯一的 router
	protected static volatile CompletableFuture<Router> routerFuture = new CompletableFuture<>();

	/**
	 * 
	 * 根据配置信息初始化 router
	 * 
	 */
	@Override
	public synchronized void initRouter(Vertx vertx, AnnotationBean annotation) {
		if (!routerFuture.isDone()) {
			Router router = this.createRouter(vertx, annotation);
			
			// 设置成功
			routerFuture.complete(router);
		}
	}

	/**
	 * 创建 router
	 * 
	 * @return
	 */
	private Router createRouter(Vertx vertx, AnnotationBean annotation) {

		// 这个时候还不能修改 唯一变量 router
		// 需要创建完成之后才行
		Router router = Router.router(vertx);

		// 路由基本配置
		for (IRouterConfig config : annotation.getRouterConfigs()) {
			config.apply(router);
		}

		// 单个路由定义
		for (RouterBean rb : annotation.getRouters()) {
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
				this.initHandler(handler);

				// 绑定处理器
				route.handler(ctx -> {
					this.handle(ctx, handler);
				});
			}
		}

		// 路由组定义
		for (IRouterSupplier rs : annotation.getRouterSuppliers()) {
			Router _router = rs.get(vertx);
			if (_router != null) {
				router.mountSubRouter(rs.path(), _router);
			}
		}

		return router;
	}

	/**
	 * 返回初始化之后的router
	 */
	@Override
	public Router getRouter() {
		try {
			return routerFuture.get();
		} catch (Exception e) {
			throw new BaseRuntimeException("初始化路由失败");
		}
	}
}