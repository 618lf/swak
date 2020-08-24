package com.swak.vertx.config;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * 提供子router
 *
 * @author: lifeng
 * @date: 2020/3/29 19:09
 */
public interface IRouterSupplier extends AbstractConfig{

    /**
     * Router
     *
     * @param vertx vertx对象
     * @return 子 Router
     */
    Router get(Vertx vertx);

    /**
     * 路径
     *
     * @return 挂载路劲
     */
    String path();
}