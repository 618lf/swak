package com.swak.rabbit;

import java.util.concurrent.CompletableFuture;

import com.swak.rabbit.message.Message;

/**
 * 消息服务 -- 服务消息应该继承此基准消息API
 * 
 * @author lifeng
 * @date 2020年10月11日 下午10:19:00
 */
public abstract class EventBusService {

	public void post(String exchange, String routingKey, Object message) {
		EventBus.me().post(exchange, routingKey, message);
	}

	public void post(String exchange, String routingKey, String message) {
		EventBus.me().post(exchange, routingKey, message);
	}

	public void post(String exchange, String routingKey, Message message) {
		EventBus.me().post(exchange, routingKey, message);
	}

	public CompletableFuture<Void> submit(String exchange, String routingKey, Object message) {
		return EventBus.me().submit(exchange, routingKey, message);
	}

	public CompletableFuture<Void> submit(String exchange, String routingKey, String message) {
		return EventBus.me().submit(exchange, routingKey, message);
	}

	public CompletableFuture<Void> submit(String exchange, String routingKey, Message message) {
		return EventBus.me().submit(exchange, routingKey, message);
	}
}