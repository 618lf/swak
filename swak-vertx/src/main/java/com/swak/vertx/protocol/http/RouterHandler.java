package com.swak.vertx.protocol.http;

import com.swak.vertx.config.AnnotationBean;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 *
 * @author: lifeng
 * @date: 2020/3/29 20:20
 */
public interface RouterHandler {


    /**
     * 根据配置信息 - 初始化router
     *
     * @param vertx      vertx对象
     * @param annotation annotation加载器
     */
    void initRouter(Vertx vertx, AnnotationBean annotation);

    /**
     * 返回初始化之后的 Router
     *
     * @return Router
     */
    Router getRouter();

    /**
     * 做一些初始化的操作
     *
     * @param handler method 处理器
     */
    default void initHandler(MethodInvoker handler) {
    }

    /**
     * 路由处理器
     *
     * @param context 请求上下文
     * @param handler 处理器
     */
    void handle(RoutingContext context, MethodInvoker handler);
}