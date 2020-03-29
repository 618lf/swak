package com.swak.utils.chain;

import java.util.concurrent.CompletionStage;

/**
 * 链式处理器中的处理器
 *
 * @author: lifeng
 * @date: 2020/3/29 13:38
 */
public interface Handler<I, O> {

    /**
     * 处理数据
     *
     * @param request 需处理的数据
     * @return 异步处理结果
     */
    CompletionStage<O> doHandle(I request);

    /**
     * 连接处理器
     *
     * @param handler 处理器
     * @return 下一个处理器
     * @author lifeng
     * @date 2020/3/29 13:39
     */
    Handler<I, O> chain(Handler<I, O> handler);
}
