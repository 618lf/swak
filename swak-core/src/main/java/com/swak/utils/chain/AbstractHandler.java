package com.swak.utils.chain;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 通用的链式处理
 *
 * @param <I>
 * @param <O>
 * @author lifeng
 */
public abstract class AbstractHandler<I, O> implements Handler<I, O> {

    Handler<I, O> handler;

    /**
     * 将整个处理器串起来
     */
    @Override
    public CompletionStage<O> doHandle(I request) {

        // 是否支持处理
        if (this.support(request)) {
            CompletionStage<O> future = this.doInnerHandle(request);
            if (future != null) {
                return future.thenCompose(res -> {
                    if (res == null) {
                        return this.doNext(request);
                    }
                    return CompletableFuture.completedFuture(res);
                });
            }
        }

        // 尝试处理下一个
        return this.doNext(request);
    }

    /**
     * 会否支持处理
     *
     * @param request 需处理的数据
     * @return 是否支持处理
     */
    protected boolean support(I request) {
        return Boolean.TRUE;
    }

    /**
     * 执行下一个 handle
     *
     * @param request 需处理的数据
     * @return 异步处理结果
     */
    private CompletionStage<O> doNext(I request) {
        if (this.handler != null) {
            return handler.doHandle(request);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 链式处理 --- 返回下一个处理器
     */
    @Override
    public Handler<I, O> chain(Handler<I, O> handler) {
        this.handler = handler;
        return handler;
    }

    /**
     * 子类需要实现的业务逻辑
     *
     * @param request 需处理的数据
     * @return 异步处理结果
     */
    protected abstract CompletionStage<O> doInnerHandle(I request);
}
