package com.swak.vertx.protocol.http;

import com.swak.exception.BaseRuntimeException;
import com.swak.vertx.config.AnnotationBean;
import com.swak.vertx.config.IRouterConfig;
import com.swak.vertx.config.IRouterSupplier;
import com.swak.vertx.config.RouterBean;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 默认路由处理
 *
 * @author: lifeng
 * @date: 2020/3/29 19:36
 */
public abstract class AbstractRouterHandler implements RouterHandler {

    protected CompletableFuture<Router> routerFuture = new CompletableFuture<>();
    protected Logger logger = LoggerFactory.getLogger(RouterHandler.class);

    /**
     * 根据配置信息初始化 Router
     */
    @Override
    public synchronized void initRouter(Vertx vertx, AnnotationBean annotation) {
        if (!routerFuture.isDone()) {
            // 初始化
            Router router = this.initRouter(vertx);

            // 应用路由
            this.applyRouter(router, vertx, annotation);

            // 设置成功
            routerFuture.complete(router);
        }
    }

    /**
     * 初始化 Router
     */
    private Router initRouter(Vertx vertx) {
        Router router = Router.router(vertx);
        router.errorHandler(404, new ErrorHandler(404));
        router.errorHandler(500, new ErrorHandler(500));
        return router;
    }

    /**
     * 设置路由
     */
    private void applyRouter(Router router, Vertx vertx, AnnotationBean annotation) {

        // 路由基本配置
        for (IRouterConfig config : annotation.getRouterConfigs()) {
            config.apply(annotation.getVertx(), router);
        }

        // 单个路由定义
        for (RouterBean rb : annotation.getRouters()) {
            rb.adapter(this).mounton(router);
        }

        // 路由组定义
        for (IRouterSupplier rs : annotation.getRouterSuppliers()) {
            Router childRouter = rs.get(vertx);
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