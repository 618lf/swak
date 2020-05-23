package com.swak.vertx.security.handler;

import com.swak.vertx.transport.Subject;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletionStage;

/**
 * 权限处理器
 *
 * @author: lifeng
 * @date: 2020/3/29 20:34
 */
public interface Handler {

    /**
     * handle 执行链获取的路径
     */
    String CHAIN_RESOLVE_PATH = "CHAIN_RESOLVE_PATH";

    /**
     * 执行处理
     *
     * @param context 请求上下文
     * @param subject 用户主体
     * @param chain   处理链
     * @return 异步结果
     */
    CompletionStage<Boolean> handle(RoutingContext context, Subject subject, HandlerChain chain);
}
