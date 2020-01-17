package com.swak.utils.chain;

import java.util.concurrent.CompletionStage;

public interface Handler<I, O> {

	/**
	 * 处理事件
	 * 
	 * @return
	 */
	CompletionStage<O> doHandle(I request);

	/**
	 * 下一个处理器
	 * 
	 * @param handler
	 */
	Handler<I, O> chain(Handler<I, O> handler);
}
