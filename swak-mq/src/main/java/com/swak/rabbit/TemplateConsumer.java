package com.swak.rabbit;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.swak.rabbit.RabbitMQTemplate.MessageHandler;
import com.swak.rabbit.connection.CacheChannelProxy;
import com.swak.rabbit.message.Message;

/**
 * 具有自动连接功能
 * 
 * @see 业务返回失败，进入消费重试
 * @see 业务执行异常，进入死信队列， 最好为每个队列配置死信队列
 * @see 消息只能按照顺序一个一个的处理，如果这个消费者在处理一个消息，没有应答，则不会处理下一个消息。
 * @see 虽然支持异步消费，但是此消息每处理完之前不会处理下一个消息
 * 
 * @author lifeng
 */
class TemplateConsumer implements Consumer {

	private final Logger logger = LoggerFactory.getLogger(TemplateConsumer.class);
	private final String queue;
	private final RabbitMQTemplate template;
	private CacheChannelProxy channel;
	private MessageHandler messageHandler;
	private final int prefetch;

	public static TemplateConsumer of(String queue, int prefetch, RabbitMQTemplate template,
			MessageHandler messageHandler) throws IOException {
		return new TemplateConsumer(queue, prefetch, template, messageHandler);
	}

	private TemplateConsumer(String queue, int prefetch, RabbitMQTemplate template, MessageHandler messageHandler)
			throws IOException {
		this.prefetch = prefetch;
		this.queue = queue;
		this.template = template;
		this.messageHandler = messageHandler;
		this.resetChannel();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
			throws IOException {
		try {
			Message message = Message.builder().setProperties(properties).setPayload(body);
			Object result = this.messageHandler.handle(message);
			if (result != null && result instanceof CompletionStage) {
				CompletionStage<Object> resultFuture = (CompletionStage<Object>) result;
				resultFuture.whenComplete((v, e) -> {
					this.handleResult(envelope, e);
				});
			} else {
				this.handleResult(envelope, null);
			}
		} catch (Exception e) {
			this.handleResult(envelope, e);
		}
	}

	// 处理消息回调
	private void handleResult(Envelope envelope, Throwable ex) {
		try {
			if (this.channel.isOpen()) {
				if (ex != null) {
					this.channel.basicNack(envelope.getDeliveryTag(), false, false);
				} else {
					this.channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		} catch (Exception e) {
			logger.error("Consumer Ack error:", e);
		}
	}

	// 充分利用已经存在的通道
	private synchronized void resetChannel() throws IOException {
		if (this.channel != null && this.channel.isOpen()) {
			this.channel.physicalClose();
		}
		this.channel = template.forChannel();
		this.channel.basicQos(prefetch);
		this.channel.basicConsume(queue, false, this);
	}

	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException cause) {
		logger.error("Consumer Shutdown:", cause);
	}

	///////////// 如下的事件不用处理 /////////////////////////////////////
	@Override
	public void handleConsumeOk(String consumerTag) {
	}

	@Override
	public void handleCancelOk(String consumerTag) {
	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		logger.error("Consumer Recover OK:", consumerTag);
	}
}