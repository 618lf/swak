package com.swak.rabbit;

import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.swak.rabbit.RabbitMQTemplate.MessageHandler;
import com.swak.rabbit.connection.CacheChannelProxy;
import com.swak.rabbit.message.Message;
import com.swak.utils.StringUtils;

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
	private ExecutorService consumerExecutor;
	private final int prefetch;

	public static TemplateConsumer of(String queue, int prefetch, RabbitMQTemplate template,
			ExecutorService consumerExecutor, MessageHandler messageHandler) throws IOException {
		return new TemplateConsumer(queue, prefetch, template, consumerExecutor, messageHandler);
	}

	private TemplateConsumer(String queue, int prefetch, RabbitMQTemplate template, ExecutorService consumerExecutor,
			MessageHandler messageHandler) throws IOException {
		this.prefetch = prefetch;
		this.queue = queue;
		this.template = template;
		this.consumerExecutor = consumerExecutor;
		this.messageHandler = messageHandler;
		this.resetChannel();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
			throws IOException {
		Message message = this.buildMessage(envelope, properties, body);
		try {
			Object result = this.messageHandler.handle(message);
			if (result != null && result instanceof CompletionStage) {
				this.asynHandleResult((CompletionStage<Object>) result, envelope, message);
			} else {
				this.handleResult(result, envelope, message, null);
			}
		} catch (Exception e) {
			this.handleError(envelope, message, e);
		}
	}

	// 创建消息
	private Message buildMessage(Envelope envelope, BasicProperties properties, byte[] body) {
		Message message = Message.builder().setProperties(properties).setPayload(body);
		if (Constants.retry_channel.equals(envelope.getExchange())) {
			return message.retryMessage();
		}
		return message;
	}

	// 异步处理消息回调
	private void asynHandleResult(CompletionStage<Object> resultFuture, Envelope envelope, Message message) {
		if (this.consumerExecutor != null) {
			resultFuture.whenCompleteAsync((v, e) -> {
				this.handleResult(v, envelope, message, e);
			}, this.consumerExecutor);
		} else {
			resultFuture.whenComplete((v, e) -> {
				this.handleResult(v, envelope, message, e);
			});
		}
	}

	// 处理消息回调
	private void handleResult(Object result, Envelope envelope, Message message, Throwable ex) {
		try {
			if (ex != null || (result != null && result instanceof Boolean && !(Boolean) result)) {
				this.handleError(envelope, message, ex);
			} else {
				this.handleSuccess(envelope, message);
			}
		} catch (Exception e) {
			logger.error("Consumer Ack error:", e);
		}
	}

	// 消息成功
	private void handleSuccess(Envelope envelope, Message message) throws IOException {
		try {
			if (this.channel.isOpen()) {
				this.channel.basicAck(envelope.getDeliveryTag(), false);
			}
			if (logger.isDebugEnabled()) {
				if (StringUtils.isBlank(message.getOrigin())) {
					logger.debug("Consume Queue[{}] - Message[{}] Success.", queue, message.getId());
				} else {
					logger.debug("Consume Queue[{}] - Message[{}] - Origin[{}] - Retry[{}] - Times[{}] Success.", queue,
							message.getId(), message.getOrigin(), message.getRetry(), message.getRetrys());
				}
			} else if (logger.isInfoEnabled()) {
				if (!StringUtils.isBlank(message.getOrigin())) {
					logger.debug("Consume Queue[{}] - Message[{}] - Origin[{}] - Retry[{}] - Times[{}] Success.", queue,
							message.getId(), message.getOrigin(), message.getRetry(), message.getRetrys());
				}
			}
		} catch (Exception e) {
			logger.error("Consumer Ack error:", e);
		}
	}

	// 消息失败
	private void handleError(Envelope envelope, Message message, Throwable ex) throws IOException {
		try {
			if (this.channel.isOpen()) {
				try {
					if (Constants.retry_channel.equals(envelope.getExchange())) {
						this.handleRetry(envelope, message);
						this.channel.basicAck(envelope.getDeliveryTag(), false);
					} else {
						this.channel.basicNack(envelope.getDeliveryTag(), false, false);
					}
				} catch (Exception e) {
					this.channel.basicNack(envelope.getDeliveryTag(), false, false);
				}
			}
			if (logger.isDebugEnabled()) {
				if (StringUtils.isBlank(message.getOrigin())) {
					logger.debug("Consume Queue[{}] - Message[{}] Error.", queue, message.getId(), ex);
				} else {
					logger.debug("Consume Queue[{}] - Message[{}] - Origin[{}] - Retry[{}] - Times[{}] Error.", queue,
							message.getId(), message.getOrigin(), message.getRetry(), message.getRetrys(), ex);
				}
			} else if (logger.isInfoEnabled()) {
				if (!StringUtils.isBlank(message.getOrigin())) {
					logger.debug("Consume Queue[{}] - Message[{}] - Origin[{}] - Retry[{}] - Times[{}] Error.", queue,
							message.getId(), message.getOrigin(), message.getRetry(), message.getRetrys(), ex);
				}
			}
		} catch (Exception e) {
			logger.error("Consumer Ack error:", e);
		}
	}

	// 发送重试消息
	private void handleRetry(Envelope envelope, Message message) throws IOException {
		int retryCount = message.getRetrys();
		BasicProperties newProperties = message.retryProperties();
		if (retryCount == 0) {
			this.channel.basicPublish(Constants.retry1s_channel, Constants.retry1s_channel, true, false, newProperties,
					message.getPayload());
		} else if (retryCount == 1) {
			this.channel.basicPublish(Constants.retry5s_channel, Constants.retry5s_channel, true, false, newProperties,
					message.getPayload());
		} else if (retryCount == 2) {
			this.channel.basicPublish(Constants.retry10s_channel, Constants.retry10s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 3) {
			this.channel.basicPublish(Constants.retry30s_channel, Constants.retry30s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 4) {
			this.channel.basicPublish(Constants.retry60s_channel, Constants.retry60s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 5) {
			this.channel.basicPublish(Constants.retry120s_channel, Constants.retry120s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 6) {
			this.channel.basicPublish(Constants.retry180s_channel, Constants.retry180s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 7) {
			this.channel.basicPublish(Constants.retry240s_channel, Constants.retry240s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 8) {
			this.channel.basicPublish(Constants.retry300s_channel, Constants.retry300s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 9) {
			this.channel.basicPublish(Constants.retry360s_channel, Constants.retry360s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 10) {
			this.channel.basicPublish(Constants.retry420s_channel, Constants.retry420s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 11) {
			this.channel.basicPublish(Constants.retry480s_channel, Constants.retry480s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 12) {
			this.channel.basicPublish(Constants.retry600s_channel, Constants.retry600s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 13) {
			this.channel.basicPublish(Constants.retry1200s_channel, Constants.retry1200s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 14) {
			this.channel.basicPublish(Constants.retry1800s_channel, Constants.retry1800s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 15) {
			this.channel.basicPublish(Constants.retry3600s_channel, Constants.retry3600s_channel, true, false,
					newProperties, message.getPayload());
		} else if (retryCount == 16) {
			this.channel.basicPublish(Constants.retry7200s_channel, Constants.retry7200s_channel, true, false,
					newProperties, message.getPayload());
		} else {
			throw new AmqpException("超过重试次数");
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