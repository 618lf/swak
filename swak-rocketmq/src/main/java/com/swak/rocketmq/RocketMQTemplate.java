package com.swak.rocketmq;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.swak.rocketmq.EventBus.TransactionHandler;
import com.swak.rocketmq.exception.MessagingException;
import com.swak.rocketmq.message.Message;
import com.swak.rocketmq.transaction.RocketMQLocalTransactionListener;
import com.swak.rocketmq.transaction.RocketMQLocalTransactionState;
import com.swak.utils.ConcurrentHashSet;

public class RocketMQTemplate implements InitializingBean, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(RocketMQTemplate.class);

	private DefaultMQProducer producer;

	private String charset = "UTF-8";

	private MessageQueueSelector messageQueueSelector = new SelectMessageQueueByHash();

	private final Map<String, TransactionMQProducer> cache = new ConcurrentHashMap<>(); // only put
																						// TransactionMQProducer by
																						// now!!!
	private final Set<String> listenerContainers = new ConcurrentHashSet<>();

	public DefaultMQProducer getProducer() {
		return producer;
	}

	public void setProducer(DefaultMQProducer producer) {
		this.producer = producer;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public MessageQueueSelector getMessageQueueSelector() {
		return messageQueueSelector;
	}

	public void setMessageQueueSelector(MessageQueueSelector messageQueueSelector) {
		this.messageQueueSelector = messageQueueSelector;
	}

	/**
	 * <p>
	 * Send message in synchronous mode. This method returns only when the sending
	 * procedure totally completes. Reliable synchronous transmission is used in
	 * extensive scenes, such as important notification messages, SMS notification,
	 * SMS marketing system, etc..
	 * </p>
	 * <p>
	 * <strong>Warn:</strong> this method has internal retry-mechanism, that is,
	 * internal implementation will retry
	 * {@link DefaultMQProducer#getRetryTimesWhenSendFailed} times before claiming
	 * failure. As a result, multiple messages may potentially delivered to
	 * broker(s). It's up to the application developers to resolve potential
	 * duplication issue.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @return {@link SendResult}
	 */
	public SendResult syncSend(String destination, Message message) {
		return syncSend(destination, message, producer.getSendMsgTimeout());
	}

	/**
	 * Same to {@link #syncSend(String, Message)} with send timeout specified in
	 * addition.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @param timeout
	 *            send timeout with millis
	 * @return {@link SendResult}
	 */
	public SendResult syncSend(String destination, Message message, long timeout) {
		return syncSend(destination, message, timeout, 0);
	}

	/**
	 * Same to {@link #syncSend(String, Message)} with send timeout specified in
	 * addition.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @param timeout
	 *            send timeout with millis
	 * @param delayLevel
	 *            level for the delay message
	 * @return {@link SendResult}
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
	 * Same to {@link #syncSend(String, Message)} with send orderly with hashKey by
	 * specified.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @param hashKey
	 *            use this key to select queue. for example: orderId, productId ...
	 * @return {@link SendResult}
	 */
	public SendResult syncSendOrderly(String destination, Message message, String hashKey) {
		return syncSendOrderly(destination, message, hashKey, producer.getSendMsgTimeout());
	}

	/**
	 * Same to {@link #syncSendOrderly(String, Message, String)} with send timeout
	 * specified in addition.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @param hashKey
	 *            use this key to select queue. for example: orderId, productId ...
	 * @param timeout
	 *            send timeout with millis
	 * @return {@link SendResult}
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
	 * Same to {@link #asyncSend(String, Message, SendCallback)} with send timeout
	 * specified in addition.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @param sendCallback
	 *            {@link SendCallback}
	 * @param timeout
	 *            send timeout with millis
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
	 * <p>
	 * Send message to broker asynchronously. asynchronous transmission is generally
	 * used in response time sensitive business scenarios.
	 * </p>
	 * <p>
	 * This method returns immediately. On sending completion,
	 * <code>sendCallback</code> will be executed.
	 * <p>
	 * Similar to {@link #syncSend(String, Object)}, internal implementation would
	 * potentially retry up to
	 * {@link DefaultMQProducer#getRetryTimesWhenSendAsyncFailed} times before
	 * claiming sending failure, which may yield message duplication and application
	 * developers are the one to resolve this potential issue.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @param sendCallback
	 *            {@link SendCallback}
	 */
	public void asyncSend(String destination, Message message, SendCallback sendCallback) {
		asyncSend(destination, message, sendCallback, producer.getSendMsgTimeout());
	}

	/**
	 * Same to {@link #asyncSendOrderly(String, Message, String, SendCallback)} with
	 * send timeout specified in addition.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @param hashKey
	 *            use this key to select queue. for example: orderId, productId ...
	 * @param sendCallback
	 *            {@link SendCallback}
	 * @param timeout
	 *            send timeout with millis
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
	 * Same to {@link #asyncSend(String, Message, SendCallback)} with send orderly
	 * with hashKey by specified.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @param hashKey
	 *            use this key to select queue. for example: orderId, productId ...
	 * @param sendCallback
	 *            {@link SendCallback}
	 */
	public void asyncSendOrderly(String destination, Message message, String hashKey, SendCallback sendCallback) {
		asyncSendOrderly(destination, message, hashKey, sendCallback, producer.getSendMsgTimeout());
	}

	/**
	 * Similar to
	 * <a href="https://en.wikipedia.org/wiki/User_Datagram_Protocol">UDP</a>, this
	 * method won't wait for acknowledgement from broker before return. Obviously,
	 * it has maximums throughput yet potentials of message loss.
	 * <p>
	 * One-way transmission is used for cases requiring moderate reliability, such
	 * as log collection.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
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
	 * Same to {@link #sendOneWay(String, Message)} with send orderly with hashKey
	 * by specified.
	 *
	 * @param destination
	 *            formats: `topicName:tags`
	 * @param message
	 *            {@link org.springframework.messaging.Message}
	 * @param hashKey
	 *            use this key to select queue. for example: orderId, productId ...
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

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(producer, "Property 'producer' is required");
		producer.start();
	}

	@Override
	public void destroy() {
		if (Objects.nonNull(producer)) {
			producer.shutdown();
		}

		for (Map.Entry<String, TransactionMQProducer> kv : cache.entrySet()) {
			if (Objects.nonNull(kv.getValue())) {
				kv.getValue().shutdown();
			}
		}
		cache.clear();
		listenerContainers.clear();
	}

	private String getTxProducerGroupName(String name) {
		return name == null ? "rocketmq_transaction_default_global_name" : name;
	}

	private TransactionMQProducer stageMQProducer(String name) throws MessagingException {
		name = getTxProducerGroupName(name);

		TransactionMQProducer cachedProducer = cache.get(name);
		if (cachedProducer == null) {
			throw new MessagingException(String.format(
					"Can not found MQProducer '%s' in cache! please define @RocketMQLocalTransactionListener class or invoke createOrGetStartedTransactionMQProducer() to create it firstly",
					name));
		}

		return cachedProducer;
	}

	/**
	 * Send Spring Message in Transaction
	 *
	 * @param txProducerGroup
	 *            the validate txProducerGroup name, set null if using the default
	 *            name
	 * @param destination
	 *            destination formats: `topicName:tags`
	 * @param message
	 *            message {@link org.springframework.messaging.Message}
	 * @param arg
	 *            ext arg
	 * @return TransactionSendResult
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

	/**
	 * Remove a TransactionMQProducer from cache by manual.
	 * <p>
	 * Note: RocketMQTemplate can release all cached producers when bean destroying,
	 * it is not recommended to directly use this method by user.
	 *
	 * @param txProducerGroup
	 * @throws MessagingException
	 */
	public void removeTransactionMQProducer(String txProducerGroup) throws MessagingException {
		txProducerGroup = getTxProducerGroupName(txProducerGroup);
		if (cache.containsKey(txProducerGroup)) {
			DefaultMQProducer cachedProducer = cache.get(txProducerGroup);
			cachedProducer.shutdown();
			cache.remove(txProducerGroup);
		}
	}

	public void registerTransactionHandler(TransactionHandler handler) throws MQClientException {
		if (listenerContainers.contains(handler.getName())) {
			throw new MQClientException(-1,
					String.format("The transaction name [%s] has been defined in TransactionListener [%s]",
							handler.getName(), handler.getBeanName()));
		}
		listenerContainers.add(handler.getName());

		this.createAndStartTransactionMQProducer(handler.getName(), handler.getListener(), handler.getCheckExecutor());
	}

	/**
	 * Create and start a transaction MQProducer, this new producer is cached in
	 * memory.
	 * <p>
	 * Note: This method is invoked internally when processing
	 * {@code @RocketMQLocalTransactionListener}, it is not recommended to directly
	 * use this method by user.
	 *
	 * @param txProducerGroup
	 *            Producer (group) name, unique for each producer
	 * @param transactionListener
	 *            TransactoinListener impl class
	 * @param executorService
	 *            Nullable.
	 * @return true if producer is created and started; false if the named producer
	 *         already exists in cache.
	 * @throws MessagingException
	 */
	public boolean createAndStartTransactionMQProducer(String txProducerGroup,
			RocketMQLocalTransactionListener transactionListener, ExecutorService executorService)
			throws MessagingException {
		txProducerGroup = getTxProducerGroupName(txProducerGroup);
		if (cache.containsKey(txProducerGroup)) {
			log.info(String.format("get TransactionMQProducer '%s' from cache", txProducerGroup));
			return false;
		}

		TransactionMQProducer txProducer = createTransactionMQProducer(txProducerGroup, transactionListener,
				executorService);
		try {
			txProducer.start();
			cache.put(txProducerGroup, txProducer);
		} catch (MQClientException e) {
			throw new MessagingException(e.getErrorMessage(), e);
		}

		return true;
	}

	private TransactionMQProducer createTransactionMQProducer(String name, RocketMQLocalTransactionListener listener,
			ExecutorService executorService) {
		Assert.notNull(producer, "Property 'producer' is required");
		Assert.notNull(listener, "Parameter 'transactionListener' is required");
		TransactionMQProducer txProducer = new TransactionMQProducer(name);
		txProducer.setTransactionListener(new TransactionListener() {
			private LocalTransactionState convertLocalTransactionState(RocketMQLocalTransactionState state) {
				switch (state) {
				case UNKNOWN:
					return LocalTransactionState.UNKNOW;
				case COMMIT:
					return LocalTransactionState.COMMIT_MESSAGE;
				case ROLLBACK:
					return LocalTransactionState.ROLLBACK_MESSAGE;
				}

				// Never happen
				log.warn("Failed to covert enum type RocketMQLocalTransactionState.%s", state);
				return LocalTransactionState.UNKNOW;
			}

			private Message convertMessage(org.apache.rocketmq.common.message.Message message) {
				return null;
			}

			@Override
			public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
				RocketMQLocalTransactionState state = listener.checkLocalTransaction(convertMessage(messageExt));
				return convertLocalTransactionState(state);
			}

			@Override
			public LocalTransactionState executeLocalTransaction(org.apache.rocketmq.common.message.Message message,
					Object arg) {
				RocketMQLocalTransactionState state = listener.executeLocalTransaction(convertMessage(message), arg);
				return convertLocalTransactionState(state);
			}
		});

		txProducer.setNamesrvAddr(producer.getNamesrvAddr());
		if (executorService != null) {
			txProducer.setExecutorService(executorService);
		}

		txProducer.setSendMsgTimeout(producer.getSendMsgTimeout());
		txProducer.setRetryTimesWhenSendFailed(producer.getRetryTimesWhenSendFailed());
		txProducer.setRetryTimesWhenSendAsyncFailed(producer.getRetryTimesWhenSendAsyncFailed());
		txProducer.setMaxMessageSize(producer.getMaxMessageSize());
		txProducer.setCompressMsgBodyOverHowmuch(producer.getCompressMsgBodyOverHowmuch());
		txProducer.setRetryAnotherBrokerWhenNotStoreOK(producer.isRetryAnotherBrokerWhenNotStoreOK());

		return txProducer;
	}
}
