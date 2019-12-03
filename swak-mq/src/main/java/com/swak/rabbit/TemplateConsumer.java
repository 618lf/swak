package com.swak.rabbit;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
		Message message = Message.of().setProperties(properties).setPayload(body);
		if (envelope.getExchange().startsWith(Constants.retry_channel)) {
			message.retryMessage();
		}
		return this.debugMessage(message);
	}

	// 调试信息
	@SuppressWarnings("unchecked")
	private Message debugMessage(Message message) {
		if (logger.isDebugEnabled() || logger.isInfoEnabled()) {
			Object retryQueue = null;
			int retryCount = 0;
			if (message.getProperties().getHeaders() != null) {
				List<Map<String, Object>> deaths = (List<Map<String, Object>>) message.getProperties().getHeaders()
						.get("x-death");
				if (deaths != null && deaths.size() > 0) {
					retryQueue = deaths.get(0).get("queue");
					for (Map<String, Object> death : deaths) {
						if (String.valueOf(death.get("reason")).equals("expired")) {
							retryCount++;
						}
					}
				}
			}
			message.setRetry(String.valueOf(retryQueue)).setRetrys(retryCount);
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
					logger.info("Consume Queue[{}] - Message[{}] - Origin[{}] - Retry[{}] - Times[{}] Success.", queue,
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
					this.channel.basicNack(envelope.getDeliveryTag(), false, false);
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
					logger.info("Consume Queue[{}] - Message[{}] - Origin[{}] - Retry[{}] - Times[{}] Error.", queue,
							message.getId(), message.getOrigin(), message.getRetry(), message.getRetrys(), ex);
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