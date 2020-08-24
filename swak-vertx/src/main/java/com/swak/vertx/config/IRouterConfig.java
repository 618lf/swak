package com.swak.vertx.config;

import com.swak.vertx.transport.VertxProxy;

import io.vertx.ext.web.Router;

/**
 * 路由配置项目
 *
 * @author: lifeng
 * @date: 2020/3/29 19:08
 */
public interface IRouterConfig extends AbstractConfig{

    /**
     * 对 router 进行配置
     *
     * @param vertx  代理vertx
     * @param router router
     */
    void apply(VertxProxy vertx, Router router);
}
