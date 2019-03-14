package com.swak.eventbus;

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
	void onMessge(byte[] data);
}