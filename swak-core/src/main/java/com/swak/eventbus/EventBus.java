package com.swak.eventbus;

/**
 * 提供事件分发
 * @author lifeng
 */
public interface EventBus {

	/**
	 * 订阅主题
	 * 将 EventConsumer 进行订阅设置
	 */
	void subscribe();
	
	/**
	 * 分发消息
	 * @param channel
	 * @param message
	 */
	void onMessage(String channel, byte[] message);
}
