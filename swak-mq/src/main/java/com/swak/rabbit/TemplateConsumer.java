package com.swak.rabbit;

import java.io.IOException;

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
 * @author lifeng
 */
public class TemplateConsumer implements Consumer {

	private final Logger logger = LoggerFactory.getLogger(TemplateConsumer.class);
	private final String queue;
	private final RabbitMQTemplate template;
	private CacheChannelProxy channel;
	private MessageHandler messageHandler;
	private final int prefetch;

	public TemplateConsumer(String queue, int prefetch, RabbitMQTemplate template, MessageHandler messageHandler)
			throws IOException {
		this.prefetch = prefetch;
		this.queue = queue;
		this.template = template;
		this.messageHandler = messageHandler;
		this.resetChannel();
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
			throws IOException {
		boolean success = true;
		try {
			Message message = Message.builder().setProperties(properties).setPayload(body);
			success = this.messageHandler.handle(message);
		} catch (Exception e) {
			success = false;
		}
		try {
			if (this.channel.isOpen()) {
				if (success) {
					this.channel.basicAck(envelope.getDeliveryTag(), false);
				} else {
					this.channel.basicNack(envelope.getDeliveryTag(), false, true);
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