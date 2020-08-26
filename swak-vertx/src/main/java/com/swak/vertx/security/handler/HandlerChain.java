package com.swak.vertx.security.handler;

import java.util.concurrent.CompletionStage;

import com.swak.security.Subject;
import com.swak.vertx.security.Context;

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
    CompletionStage<Boolean> doHandler(Context context, Subject subject);
}
