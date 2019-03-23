package com.swak.rabbit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.swak.rabbit.message.Message;
import com.swak.rabbit.message.PendingConfirm;
import com.swak.rabbit.retry.RetryStrategy;

/**
 * 发送消息， 失败重试功能
 * 
 * @author lifeng
 */
public class TemplateSender {

	private RabbitMQTemplate template;
	private RetryStrategy retry;
	private String exchange;
	private String routingKey;

	public TemplateSender setTemplate(RabbitMQTemplate template) {
		this.template = template;
		return this;
	}

	public TemplateSender setRetry(RetryStrategy retry) {
		this.retry = retry;
		return this;
	}

	public TemplateSender setExchange(String exchange) {
		this.exchange = exchange;
		return this;
	}

	public TemplateSender setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
		return this;
	}

	/**
	 * 异步发送消息
	 * 
	 * @param message
	 * @return
	 */
	public CompletableFuture<Void> send(Message message, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			this.send(message);
		}, executor);
	}

	/**
	 * 发送消息
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public void send(Message message) {
		PendingConfirm pendingConfirm = null;
		try {
			pendingConfirm = this.template.basicPublish(exchange, routingKey, message);
		} catch (Exception e) {
			pendingConfirm = new PendingConfirm(message.getId());
		}
		if (this.retry != null) {
			this.bindPendingConfirm(pendingConfirm, message);
			this.retry.add(pendingConfirm);
		}
	}

	// 将 message 绑定到 pendingConfirm 中
	private void bindPendingConfirm(PendingConfirm pendingConfirm, Message message) {
		pendingConfirm.setDeliveryMode(message.getProperties().getDeliveryMode());
		pendingConfirm.setPriority(message.getProperties().getPriority());
		pendingConfirm.setExpiration(message.getProperties().getExpiration());
		pendingConfirm.setExchange(exchange);
		pendingConfirm.setRoutingKey(routingKey);
		pendingConfirm.setPayload(message.getPayload());
	}

	/**
	 * 创建发送器
	 * 
	 * @return
	 */
	public static TemplateSender me() {
		return new TemplateSender();
	}
}