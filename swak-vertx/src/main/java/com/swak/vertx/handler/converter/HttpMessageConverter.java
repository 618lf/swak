package com.swak.vertx.handler.converter;

import io.vertx.core.http.HttpServerResponse;

/**
 * Http Message 消息转换器
 *
 * @author: lifeng
 * @date: 2020/3/29 19:19
 */
public interface HttpMessageConverter {

    /**
     * 写数据的判断
     *
     * @param clazz 类型
     * @return 写数据的判断
     */
    boolean canWrite(Class<?> clazz);

    /**
     * 输出响应
     *
     * @param t        输出对象
     * @param response 响应
     */
    void write(Object t, HttpServerResponse response);
}
