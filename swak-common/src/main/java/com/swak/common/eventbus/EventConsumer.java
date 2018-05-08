package com.swak.common.eventbus;

/**
 * 
 * @author lifeng
 */
public interface EventConsumer {

	/**
	 * 订阅通道
	 * @return
	 */
	String getChannel();
	
	/**
	 * 消费消息
	 * @param message
	 */
	void onMessge(Event event);
}