package com.sample.publisher;

import java.util.concurrent.CompletableFuture;

import com.sample.api.event.GoodsEvent;

/**
 * 发送消息
 * 
 * @author lifeng
 */
public interface GoodsEventPublisher {

	/**
	 * 异步发送消息
	 * 
	 * @param queue
	 * @param event
	 * @return
	 */
	CompletableFuture<Void> post(GoodsEvent event);
}