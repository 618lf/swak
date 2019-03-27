package com.swak.rabbit.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.rabbit.RabbitMQTemplate;
import com.swak.rabbit.RabbitMQTemplate.ConfirmCallback;
import com.swak.rabbit.RabbitMQTemplate.ReturnCallback;
import com.swak.rabbit.message.Message;
import com.swak.rabbit.message.PendingConfirm;

/**
 * 消息重试机制
 * 
 * @author lifeng
 */
public interface RetryStrategy extends ConfirmCallback, ReturnCallback {

	// 专有的日志
	Logger LOGGER = LoggerFactory.getLogger(RetryStrategy.class);
	
	/**
	 * 绑定消息发送器
	 * 
	 * @param sender
	 */
	void bindSender(RabbitMQTemplate template);
	
	/**
	 * 获得消息发送器
	 * 
	 * @param sender
	 */
	RabbitMQTemplate getSender();
	
	/**
	 * 添加需要重试的消息
	 * 
	 * @param message
	 */
	void add(PendingConfirm pendingConfirm);
	
	/**
	 * 删除已经确认的消息
	 * 
	 * @param id 全局的消息ID
	 */
	void del(String id);
	
	/**
	 * 确认消息发送成功，清除待重试的消息
	 */
	default void confirm(PendingConfirm pendingConfirm, boolean ack) {
		if (ack) {
			this.del(pendingConfirm.getId());
		}
	}

	/**
	 * 如果消息沒有送到指定的队列，重新发送消息
	 */
	default void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		getSender().basicPublish(exchange, routingKey, message);
	}
}