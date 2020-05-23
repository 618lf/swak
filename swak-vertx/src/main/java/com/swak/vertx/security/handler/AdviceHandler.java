package com.swak.vertx.security.handler;

import com.swak.vertx.transport.Subject;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 访问控制器
 *
 * @author: lifeng
 * @date: 2020/3/29 20:35
 */
public abstract class AdviceHandler implements Handler {

    /**
     * 执行处理
     */
    @Override
    public CompletionStage<Boolean> handle(RoutingContext context, Subject subject, HandlerChain chain) {
        return this.isAccessDenied(context, subject).thenCompose(allowed -> {
            if (!allowed) {
                return this.onAccessDenied(context, subject);
            }
            return CompletableFuture.completedFuture(true);
        }).thenCompose(allowed -> {
            if (allowed) {
                return chain.doHandler(context, subject);
            }
            return CompletableFuture.completedFuture(false);
        });
    }

    /**
     * 是否继续执行
     *
     * @param context 请求上下文
     * @param subject 用户主体
     * @return 异步结果
     */
    public CompletionStage<Boolean> isAccessDenied(RoutingContext context, Subject subject) {
        return CompletableFuture.completedFuture(true);
    }

    /**
     * 如果不继续执行则怎么处理
     *
	 * @param context 请求上下文
	 * @param subject 用户主体
	 * @return 异步结果
     */
    public CompletionStage<Boolean> onAccessDenied(RoutingContext context, Subject subject) {
        return CompletableFuture.completedFuture(true);
    }
}
