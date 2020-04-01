package com.swak.vertx.security.handler;

/**
 * 参数配置
 *
 * @author: lifeng
 * @date: 2020/3/29 20:33
 */
public interface PathDefinition {

    /**
     * 路径配置
     *
     * @param path  url
     * @param param 参数
     */
    void pathConfig(String path, String param);
}