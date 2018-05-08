package com.swak.common.eventbus;

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
	void publish(String channel, byte[] message);
	
	/**
	 * 发布消息
	 * @param channel
	 * @param obj
	 */
	void publish(String channel, Object obj);
}