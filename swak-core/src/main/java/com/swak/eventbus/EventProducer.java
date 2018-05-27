package com.swak.eventbus;

import java.util.concurrent.CompletionStage;

/**
 * 事件生产者
 * @author lifeng
 */
public interface EventProducer {

	/**
	 * 发布消息
	 * @param channel
	 * @param message
	 */
	CompletionStage<Long> publish(String channel, byte[] message);
	
	/**
	 * 发布消息
	 * @param channel
	 * @param obj
	 */
	CompletionStage<Long> publish(String channel, Object obj);
}