package com.swak.rocketmq;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.swak.rocketmq.EventBus.TransactionHandler;
import com.swak.rocketmq.exception.MessagingException;
import com.swak.rocketmq.message.Message;

/**
 * 消息发送模板，支持同步，异步，有序，无序，事务等消息的发送
 * 
 * @author lifeng
 */
public class RocketMQTemplate implements InitializingBean, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(RocketMQTemplate.class);
	private DefaultMQProducer producer;
	private MessageQueueSelector messageQueueSelector = new SelectMessageQueueByHash();
	private final Map<String, TransactionMQProducer> transactionProducers = new ConcurrentHashMap<>();

	public DefaultMQProducer getProducer() {
		return producer;
	}

	public void setProducer(DefaultMQProducer producer) {
		this.producer = producer;
	}

	/**
	 * 发送消息
	 * 
	 * @param destination
	 * @param message
	 * @return
	 */
	public SendResult syncSend(String destination, Message message) {
		return syncSend(destination, message, producer.getSendMsgTimeout());
	}

	/**
	 * 发送消息
	 * 
	 * @param destination
	 * @param message
	 * @param timeout
	 * @return
	 */
	public SendResult syncSend(String destination, Message message, long timeout) {
		return syncSend(destination, message, timeout, 0);
	}

	/**
	 * 发送消息
	 * 
	 * @param destination
	 * @param message
	 * @param timeout
	 * @param delayLevel
	 * @return
	 */
	public SendResult syncSend(String destination, Message message, long timeout, int delayLevel) {
		if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
			log.error("syncSend failed. destination:{}, message is null ", destination);
			throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
		}

		try {
			long now = System.currentTimeMillis();
			org.apache.rocketmq.common.message.Message rocketMsg = message.to();
			if (delayLevel > 0) {
				rocketMsg.setDelayTimeLevel(delayLevel);
			}
			SendResult sendResult = producer.send(rocketMsg, timeout);
			long costTime = System.currentTimeMillis() - now;
			log.debug("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
			return sendResult;
		} catch (Exception e) {
			log.error("syncSend failed. destination:{}, message:{} ", destination, message);
			throw new MessagingException(e.getMessage(), e);
		}
	}

	/**
	 * 发送需要消费顺序消息
	 * 
	 * @param destination
	 * @param message
	 * @param hashKey
	 * @return
	 */
	public SendResult syncSendOrderly(String destination, Message message, String hashKey) {
		return syncSendOrderly(destination, message, hashKey, producer.getSendMsgTimeout());
	}

	/**
	 * 发送需要消费顺序消息
	 * 
	 * @param destination
	 * @param message
	 * @param hashKey
	 * @param timeout
	 * @return
	 */
	public SendResult syncSendOrderly(String destination, Message message, String hashKey, long timeout) {
		if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
			log.error("syncSendOrderly failed. destination:{}, message is null ", destination);
			throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
		}

		try {
			long now = System.currentTimeMillis();
			org.apache.rocketmq.common.message.Message rocketMsg = message.to();
			SendResult sendResult = producer.send(rocketMsg, messageQueueSelector, hashKey, timeout);
			long costTime = System.currentTimeMillis() - now;
			log.debug("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
			return sendResult;
		} catch (Exception e) {
			log.error("syncSendOrderly failed. destination:{}, message:{} ", destination, message);
			throw new MessagingException(e.getMessage(), e);
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param destination
	 * @param message
	 * @param sendCallback
	 */
	public void asyncSend(String destination, Message message, SendCallback sendCallback) {
		asyncSend(destination, message, sendCallback, producer.getSendMsgTimeout());
	}
	
	/**
	 * 发送消息
	 * 
	 * @param destination
	 * @param message
	 * @param sendCallback
	 * @param timeout
	 */
	public void asyncSend(String destination, Message message, SendCallback sendCallback, long timeout) {
		if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
			log.error("asyncSend failed. destination:{}, message is null ", destination);
			throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
		}

		try {
			org.apache.rocketmq.common.message.Message rocketMsg = message.to();
			producer.send(rocketMsg, sendCallback, timeout);
		} catch (Exception e) {
			log.info("asyncSend failed. destination:{}, message:{} ", destination, message);
			throw new MessagingException(e.getMessage(), e);
		}
	}

	/**
	 * 发送需要消费顺序的消息
	 * 
	 * @param destination
	 * @param message
	 * @param hashKey
	 * @param sendCallback
	 * @param timeout
	 */
	public void asyncSendOrderly(String destination, Message message, String hashKey, SendCallback sendCallback,
			long timeout) {
		if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
			log.error("asyncSendOrderly failed. destination:{}, message is null ", destination);
			throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
		}

		try {
			org.apache.rocketmq.common.message.Message rocketMsg = message.to();
			producer.send(rocketMsg, messageQueueSelector, hashKey, sendCallback, timeout);
		} catch (Exception e) {
			log.error("asyncSendOrderly failed. destination:{}, message:{} ", destination, message);
			throw new MessagingException(e.getMessage(), e);
		}
	}

	/**
	 * 发送需要消费顺序的消息
	 * 
	 * @param destination
	 * @param message
	 * @param hashKey
	 * @param sendCallback
	 */
	public void asyncSendOrderly(String destination, Message message, String hashKey, SendCallback sendCallback) {
		asyncSendOrderly(destination, message, hashKey, sendCallback, producer.getSendMsgTimeout());
	}

	/**
	 * 发送无返回结果的消息
	 * 
	 * @param destination
	 * @param message
	 */
	public void sendOneWay(String destination, Message message) {
		if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
			log.error("sendOneWay failed. destination:{}, message is null ", destination);
			throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
		}

		try {
			org.apache.rocketmq.common.message.Message rocketMsg = message.to();
			producer.sendOneway(rocketMsg);
		} catch (Exception e) {
			log.error("sendOneWay failed. destination:{}, message:{} ", destination, message);
			throw new MessagingException(e.getMessage(), e);
		}
	}

	/**
	 * 发送无返回结果的消息
	 * 
	 * @param destination
	 * @param message
	 * @param hashKey
	 */
	public void sendOneWayOrderly(String destination, Message message, String hashKey) {
		if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
			log.error("sendOneWayOrderly failed. destination:{}, message is null ", destination);
			throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
		}

		try {
			org.apache.rocketmq.common.message.Message rocketMsg = message.to();
			producer.sendOneway(rocketMsg, messageQueueSelector, hashKey);
		} catch (Exception e) {
			log.error("sendOneWayOrderly failed. destination:{}, message:{}", destination, message);
			throw new MessagingException(e.getMessage(), e);
		}
	}

	/**
	 * 发送事务消息
	 * 
	 * @param txProducerGroup
	 * @param destination
	 * @param message
	 * @param arg
	 * @return
	 * @throws MessagingException
	 */
	public TransactionSendResult sendMessageInTransaction(final String txProducerGroup, final String destination,
			final Message message, final Object arg) throws MessagingException {
		try {
			TransactionMQProducer txProducer = this.stageMQProducer(txProducerGroup);
			org.apache.rocketmq.common.message.Message rocketMsg = message.to();
			return txProducer.sendMessageInTransaction(rocketMsg, arg);
		} catch (MQClientException e) {
			throw new MessagingException(e.getErrorMessage(), e);
		}
	}

	private TransactionMQProducer stageMQProducer(String txProducerGroup) throws MessagingException {
		TransactionMQProducer cachedProducer = transactionProducers.get(txProducerGroup);
		if (cachedProducer == null) {
			throw new MessagingException(String.format(
					"Can not found MQProducer '%s' in cache! please define @RocketMQLocalTransactionListener class or invoke createOrGetStartedTransactionMQProducer() to create it firstly",
					txProducerGroup));
		}

		return cachedProducer;
	}

	/**
	 * 注册事务处理器
	 * 
	 * @param handler
	 * @throws MQClientException
	 */
	public void registerTransactionHandler(TransactionHandler handler) {
		String txProducerGroup = handler.getName();
		if (transactionProducers.containsKey(txProducerGroup)) {
			log.info(String.format("get TransactionMQProducer '%s' from cache", txProducerGroup));
			return;
		}

		TransactionMQProducer txProducer = createTransactionMQProducer(handler);
		try {
			txProducer.start();
			transactionProducers.put(txProducerGroup, txProducer);
		} catch (MQClientException e) {
			throw new MessagingException(e.getErrorMessage(), e);
		}
	}

	private TransactionMQProducer createTransactionMQProducer(TransactionHandler handler) {
		Assert.notNull(producer, "Property 'producer' is required");
		TransactionMQProducer txProducer = new TransactionMQProducer(handler.getName());
		txProducer.setTransactionListener(handler);

		txProducer.setNamesrvAddr(producer.getNamesrvAddr());
		if (handler.getCheckExecutor() != null) {
			txProducer.setExecutorService(handler.getCheckExecutor());
		}
		txProducer.setSendMsgTimeout(producer.getSendMsgTimeout());
		txProducer.setRetryTimesWhenSendFailed(producer.getRetryTimesWhenSendFailed());
		txProducer.setRetryTimesWhenSendAsyncFailed(producer.getRetryTimesWhenSendAsyncFailed());
		txProducer.setMaxMessageSize(producer.getMaxMessageSize());
		txProducer.setCompressMsgBodyOverHowmuch(producer.getCompressMsgBodyOverHowmuch());
		txProducer.setRetryAnotherBrokerWhenNotStoreOK(producer.isRetryAnotherBrokerWhenNotStoreOK());
		return txProducer;
	}

	/**
	 * 启动默认的生产者
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(producer, "Property 'producer' is required");
		producer.start();
	}

	/**
	 * 销毁所有的生产者
	 */
	@Override
	public void destroy() {
		if (Objects.nonNull(producer)) {
			producer.shutdown();
		}

		for (Map.Entry<String, TransactionMQProducer> kv : transactionProducers.entrySet()) {
			if (Objects.nonNull(kv.getValue())) {
				kv.getValue().shutdown();
			}
		}
		transactionProducers.clear();
	}
}
