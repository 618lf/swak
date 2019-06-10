package com.swak.rabbit;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MetricsCollector;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import com.swak.rabbit.connection.CacheChannelProxy;
import com.swak.rabbit.connection.CacheChannelProxy.Listener;
import com.swak.rabbit.message.Message;
import com.swak.rabbit.message.PendingConfirm;

/**
 * 管理整个连接, 和所有的 Channel
 * 
 * @author lifeng
 */
public class RabbitMQTemplate
		implements ShutdownListener, DisposableBean, ApplicationListener<ContextClosedEvent>, Listener {

	private final Logger logger = LoggerFactory.getLogger(RabbitMQTemplate.class);
	private final RabbitMQProperties config;
	private ConnectionFactory connectionFactory;
	private Connection connection;
	private final TransferQueue<CacheChannelProxy> channels;
	private final Object connectionMonitor = new Object();
	private ConfirmCallback confirmCallback;
	private ReturnCallback returnCallback;
	private volatile boolean stopped;
	private ExecutorService consumerWorkServiceExecutor;
	private ThreadFactory daemonFactory;
	private ExecutorService shutdownExecutor;
	private ScheduledExecutorService heartbeatExecutor;
	private ExecutorService topologyRecoveryExecutor;
	private MetricsCollector metricsCollector;

	public RabbitMQTemplate(RabbitMQProperties config) {
		this.config = config;
		this.channels = new LinkedTransferQueue<>();
	}

	// 设置共享的消费者执行器 （并不每个连接都需要）
	public RabbitMQTemplate setConsumerWorkServiceExecutor(ExecutorService consumerWorkServiceExecutor) {
		this.consumerWorkServiceExecutor = consumerWorkServiceExecutor;
		return this;
	}

	public RabbitMQTemplate setDaemonFactory(ThreadFactory daemonFactory) {
		this.daemonFactory = daemonFactory;
		return this;
	}

	public RabbitMQTemplate setShutdownExecutor(ExecutorService shutdownExecutor) {
		this.shutdownExecutor = shutdownExecutor;
		return this;
	}

	public RabbitMQTemplate setHeartbeatExecutor(ScheduledExecutorService heartbeatExecutor) {
		this.heartbeatExecutor = heartbeatExecutor;
		return this;
	}

	public RabbitMQTemplate setTopologyRecoveryExecutor(ExecutorService topologyRecoveryExecutor) {
		this.topologyRecoveryExecutor = topologyRecoveryExecutor;
		return this;
	}

	public RabbitMQTemplate setMetricsCollector(MetricsCollector metricsCollector) {
		this.metricsCollector = metricsCollector;
		return this;
	}

	public RabbitMQTemplate setConfirmCallback(ConfirmCallback confirmCallback) {
		this.confirmCallback = confirmCallback;
		return this;
	}

	public RabbitMQTemplate setReturnCallback(ReturnCallback returnCallback) {
		this.returnCallback = returnCallback;
		return this;
	}

	/**
	 * 创建连接管理器
	 * 
	 * @return
	 * @throws AmqpException
	 */
	protected ConnectionFactory newConnectionFactory() throws AmqpException {
		ConnectionFactory cf = new ConnectionFactory();
		String uri = config.getUri();
		if (uri != null) {
			try {
				cf.setUri(uri);
			} catch (Exception e) {
				logger.error("Invalid rabbitmq connection uri " + uri);
				throw new AmqpException("Invalid rabbitmq connection uri " + uri);
			}
		} else {
			cf.setUsername(config.getUser());
			cf.setPassword(config.getPassword());
			cf.setVirtualHost(config.getVirtualHost());
		}
		cf.setSharedExecutor(consumerWorkServiceExecutor);
		cf.setHeartbeatExecutor(heartbeatExecutor);
		cf.setShutdownExecutor(shutdownExecutor);
		cf.setTopologyRecoveryExecutor(topologyRecoveryExecutor);
		cf.setHeartbeatExecutor(heartbeatExecutor);
		cf.setThreadFactory(daemonFactory);
		cf.setMetricsCollector(metricsCollector);
		cf.setConnectionTimeout(config.getConnectionTimeout());
		cf.setRequestedHeartbeat(config.getRequestedHeartbeat());
		cf.setHandshakeTimeout(config.getHandshakeTimeout());
		cf.setRequestedChannelMax(config.getRequestedChannelMax());
		cf.setNetworkRecoveryInterval(config.getNetworkRecoveryInterval());
		cf.setAutomaticRecoveryEnabled(config.isAutomaticRecoveryEnabled());
		return cf;
	}

	/**
	 * 创建连接 (真实的连接)
	 * 
	 * @return
	 * @throws IOException
	 * @throws TimeoutException
	 */
	private Connection newConnection() throws AmqpException {
		// 如果自动恢复则不创建新的连接
		if (this.connection != null && connectionFactory.isAutomaticRecoveryEnabled()) {
			return this.connection;
		}
		List<Address> addresses = config.getAddresses().isEmpty()
				? Collections.singletonList(new Address(config.getHost(), config.getPort()))
				: config.getAddresses();
		try {
			if (connectionFactory == null) {
				this.connectionFactory = this.newConnectionFactory();
			}
			Connection connection = addresses == null ? connectionFactory.newConnection()
					: connectionFactory.newConnection(addresses);
			connection.addShutdownListener(this);
			return connection;
		} catch (IOException | TimeoutException e) {
			throw new AmqpException(e);
		}
	}

	/**
	 * 创建通到 (真实的通到)
	 * 
	 * @return
	 * @throws IOException
	 */
	private Channel newChannel() throws IOException {
		return connection.createChannel();
	}

	/**
	 * 获取连接 (方案不能和自动恢复一起用，恢复时会抛出异常，因为已经主动关闭了通道) 消费者的连接没有主动关闭，没啥问题。
	 * 
	 * @return
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public CacheChannelProxy forChannel() throws AmqpException {
		CacheChannelProxy channel = null;
		synchronized (channels) {
			while (!channels.isEmpty()) {
				channel = channels.poll();
				if (channel.isOpen()) {
					break;
				}
				try {
					channel.physicalClose();
				} catch (Exception e) {
					logger.error("Close Channel ", e);
				}
				channel = null;
			}
		}
		if (channel == null) {
			channel = this.doCreatChannel();
			channel.addListener(this);
		}
		return channel;
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public <T> T execute(ChannelHandler<T> action) throws AmqpException {
		CacheChannelProxy channel = null;
		try {
			channel = this.forChannel();
			return action.handle(channel);
		} catch (Exception e) {
			throw new AmqpException(e);
		} finally {
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException | TimeoutException e) {
					channel.physicalClose();
				}
			}
		}
	}

	/**
	 * 设置发送需要异步返回应答。也可以使用 waitForConfirms 的方式
	 * 
	 * @return
	 * @throws IOException
	 * @throws TimeoutException
	 */
	protected CacheChannelProxy doCreatChannel() throws AmqpException {
		if (this.stopped) {
			throw new AmqpException(
					"The ApplicationContext is closed and the ConnectionFactory can no longer create connections.");
		}
		if (this.connection == null || !this.connection.isOpen()) {
			synchronized (connectionMonitor) {
				if (this.connection == null || !this.connection.isOpen()) {
					this.connection = this.newConnection();
				}
			}
		}
		try {
			Channel channel = this.newChannel();
			channel.confirmSelect();
			return new CacheChannelProxy(this.channels, channel);
		} catch (IOException e) {
			throw new AmqpException(e);
		}
	}

	// 用于处理 外部的接口
	@Override
	public void handleConfirm(PendingConfirm pendingConfirm, boolean ack) {
		if (confirmCallback != null) {
			confirmCallback.confirm(pendingConfirm, ack);
		}
	}

	@Override
	public void handleReturn(int replyCode, String replyText, String exchange, String routingKey,
			BasicProperties properties, byte[] body) throws IOException {
		if (returnCallback != null) {
			returnCallback.returnedMessage(Message.builder().setPayload(body).setProperties(properties), replyCode,
					replyText, exchange, routingKey);
		}
	}

	//////////// 三個簡單的通用的初始化方案，如果不满足可以使用 execute 自己调用channel 声明////////
	// exchange 持久化，不自动删除，非内部使用
	// queue 持久化，非排他，不自动删除, 参数设置
	public void exchangeDirectBindQueue(String exchange, String routingKey, String queue,
			Map<String, Object> queueArguments) throws AmqpException {
		execute(channel -> {
			channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, true, false, false, null);
			channel.queueDeclare(queue, true, false, false, queueArguments);
			channel.queueBind(queue, exchange, routingKey);
			return null;
		});
	}

	public void exchangeTopicBindQueue(String exchange, String routingKey, String queue,
			Map<String, Object> queueArguments) throws AmqpException {
		execute(channel -> {
			channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true, false, null);
			channel.queueDeclare(queue, true, false, false, queueArguments);
			channel.queueBind(queue, exchange, routingKey);
			return null;
		});
	}

	public void exchangeFanoutBindQueue(String exchange, String routingKey, String queue,
			Map<String, Object> queueArguments) throws AmqpException {
		execute(channel -> {
			channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true, false, null);
			channel.queueDeclare(queue, true, false, false, queueArguments);
			channel.queueBind(queue, exchange, routingKey);
			return null;
		});
	}

	/////////////////// 簡單的發送消息/////////
	public PendingConfirm basicPublish(String exchange, String routingKey, Message message) throws AmqpException {
		return basicPublish(exchange, routingKey, message, true);
	}

	public PendingConfirm basicPublish(String exchange, String routingKey, Message message, boolean confirm)
			throws AmqpException {
		return execute(channel -> {
			PendingConfirm pendingConfirm = null;
			if (confirm) {
				pendingConfirm = new PendingConfirm(message.getId());
				channel.addPendingConfirm(channel.getNextPublishSeqNo(), pendingConfirm);
			}
			channel.basicPublish(exchange, routingKey, true, message.getProperties(), message.getPayload());
			return pendingConfirm;
		});
	}

	/////////////////// 簡單的消息消费/////////
	public void basicConsume(String queue, int prefetch, MessageHandler messageHandler) throws AmqpException {
		try {
			TemplateConsumer.of(queue, prefetch, this, messageHandler);
		} catch (IOException e) {
			throw new AmqpException(e);
		}
	}

	// 关闭完成
	@Override
	public void shutdownCompleted(ShutdownSignalException cause) {
		logger.error("Connection shutdown", cause);
	}

	/**
	 * 处理事件
	 * 
	 * @param <T>
	 */
	@FunctionalInterface
	public interface ChannelHandler<T> {
		T handle(CacheChannelProxy channel) throws IOException;
	}

	/**
	 * 处理消息
	 * 
	 * @param <T>
	 */
	@FunctionalInterface
	public interface MessageHandler {
		boolean handle(Message message) throws AmqpException;
	}

	/**
	 * 处理发送应答
	 *
	 */
	@FunctionalInterface
	public interface ConfirmCallback {
		void confirm(PendingConfirm pendingConfirm, boolean ack);
	}

	/**
	 * 处理不可答返回
	 *
	 */
	@FunctionalInterface
	public interface ReturnCallback {
		void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey);
	}

	/**
	 * 等待关闭
	 */
	@Override
	public void destroy() throws Exception {
		synchronized (this.connectionMonitor) {
			if (this.connection != null) {
				try {
					this.connection.close();
				} catch (Exception e) {
					logger.trace("Could not close Connection", e);
				}
			}
			synchronized (channels) {
				for (CacheChannelProxy channel : channels) {
					try {
						channel.physicalClose();
					} catch (Exception ex) {
						logger.trace("Could not close cached Rabbit Channel", ex);
					}
				}
				channels.clear();
			}
		}
		this.stopped = true;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		this.stopped = true;
	}
}