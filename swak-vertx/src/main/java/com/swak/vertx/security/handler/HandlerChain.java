package com.swak.vertx.security.handler;

import com.swak.vertx.transport.Subject;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletionStage;

/**
 * handler 执行链
 *
 * @author: lifeng
 * @date: 2020/3/29 20:40
 */
public interface HandlerChain {

    /**
     * 执行 handler
     *
     * @param context 请求上下文
     * @param subject 用户主体
     * @return 异步结果
     */
    CompletionStage<Boolean> doHandler(RoutingContext context, Subject subject);
}
