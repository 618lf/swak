package com.swak.rocketmq.message;

/**
 * 需要发送的消息
 * 
 * @author lifeng
 */
public interface Message {

	/**
	 * Return the message payload.
	 */
	<T> T getPayload();
	
	/**
	 * 转为 rocket 消息
	 * 
	 * @return
	 */
	org.apache.rocketmq.common.message.Message to();
}