package com.swak.vertx.protocol.http;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.swak.exception.BaseRuntimeException;
import com.swak.vertx.config.IRouterConfig;
import com.swak.vertx.config.IRouterSupplier;
import com.swak.vertx.config.RouterBean;
import com.swak.vertx.config.VertxConfigs;
import com.swak.vertx.transport.VertxProxy;

import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * 默认路由处理
 *
 * @author: lifeng
 * @date: 2020/3/29 19:36
 */
public abstract class AbstractRouterHandler implements RouterHandler {

	protected CompletableFuture<Router> routerFuture = new CompletableFuture<>();
	protected Logger logger = LoggerFactory.getLogger(RouterHandler.class);

	@Autowired
	protected VertxProxy vertx;

	/**
	 * 返回初始化之后的router
	 */
	@Override
	public Router getRouter() {
		if (!routerFuture.isDone()) {
			initRouter();
		}
		try {
			return routerFuture.get();
		} catch (Exception e) {
			throw new BaseRuntimeException("初始化路由失败");
		}
	}

	/**
	 * 根据配置信息初始化 Router
	 */
	protected synchronized void initRouter() {
		if (!routerFuture.isDone()) {

			// 初始化
			Router router = Router.router(vertx.me());
			router.errorHandler(404, new ErrorHandler(404));
			router.errorHandler(500, new ErrorHandler(500));

			// 应用路由
			this.applyRouter(router);

			// 设置成功
			routerFuture.complete(router);
		}
	}

	/**
	 * 设置路由
	 */
	protected void applyRouter(Router router) {

		// 路由基本配置
		Set<IRouterConfig> configs = VertxConfigs.me().getRouterConfigs();
		for (IRouterConfig config : configs) {
			config.apply(vertx, router);
		}

		// 单个路由定义
		Set<RouterBean> beans = VertxConfigs.me().getRouters();
		for (RouterBean rb : beans) {
			rb.mounton(router);
		}

		// 路由组定义
		Set<IRouterSupplier> suppliers = VertxConfigs.me().getRouterSuppliers();
		for (IRouterSupplier rs : suppliers) {
			Router childRouter = rs.get(vertx.me());
			if (childRouter != null) {
				router.mountSubRouter(rs.path(), childRouter);
			}
		}

		// 打印路由信息
		if (logger.isDebugEnabled()) {
			List<Route> routes = router.getRoutes();
			for (Route route : routes) {
				logger.debug("{}\t{}", route.methods() != null ? route.methods().toString() : "All",
						route.getPath() != null ? route.getPath() : "All");
			}
		}
	}
}