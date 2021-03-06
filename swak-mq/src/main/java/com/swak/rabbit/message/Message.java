package com.swak.rabbit.message;

import java.util.UUID;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.swak.Constants;
import com.swak.rabbit.AmqpException;
import com.swak.serializer.SerializationUtils;
import com.swak.utils.StringUtils;

import io.netty.util.internal.StringUtil;

/**
 * 基础的消息 (不可序列化，仅仅作为发送消息的载体)
 * 
 * @author lifeng
 */
public class Message {

	// 基本参数
	private BasicProperties properties;
	private byte[] payload; // 消息的内容

	// 消息可以设置的参数
	private String id; // 设置到 messageId 中
	private Integer deliveryMode = 2; // 发布模式1(不持久化)， 2(持久化) 默认是持久化的消息，可以更改
	private Integer priority; // 消息的优先级 0 - 9
	private String expiration; // 消息的 ttl (s)

	// 重试
	private String origin;
	private String retry;
	private Integer retrys;

	// 发送参数 queue 才有用
	private String exchange;
	private String routingKey;

	public String getId() {
		if (id != null && properties == null) {
			throw new AmqpException("Invoke Message.build to Finish Message Init.");
		}
		if (properties != null) {
			return properties.getMessageId();
		}
		return StringUtil.EMPTY_STRING;
	}

	public Message setId(String id) {
		this.id = id;
		return this;
	}

	public Message setDeliveryMode(Integer deliveryMode) {
		this.deliveryMode = deliveryMode;
		return this;
	}

	public Message setPriority(Integer priority) {
		this.priority = priority;
		return this;
	}

	public Message setExpiration(String expiration) {
		this.expiration = expiration;
		return this;
	}

	public BasicProperties getProperties() {
		return properties;
	}

	public Message setProperties(BasicProperties properties) {
		this.properties = properties;
		return this;
	}

	public byte[] getPayload() {
		return payload;
	}

	public Message setPayload(byte[] payload) {
		this.payload = payload;
		return this;
	}

	public String getOrigin() {
		return origin;
	}

	public Integer getRetrys() {
		return retrys;
	}

	public String getRetry() {
		return retry;
	}

	public Message setOrigin(String origin) {
		this.origin = origin;
		return this;
	}

	public Message setRetrys(Integer retrys) {
		this.retrys = retrys;
		return this;
	}

	public Message setRetry(String retry) {
		this.retry = retry;
		return this;
	}

	/**
	 * 将 object 序列化为 字节码，使用默認的序列化方式
	 * 
	 * @param payload
	 * @return
	 */
	public Message object2Payload(Object payload) {
		this.payload = SerializationUtils.serialize(payload);
		return this;
	}

	/**
	 * 将 object 序列化为 字节码，使用默認的序列化方式
	 * 
	 * @param payload
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T payload2Object() {
		return (T) SerializationUtils.deserialize(payload);
	}

	public String getExchange() {
		return exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public Message setExchange(String exchange) {
		this.exchange = exchange;
		return this;
	}

	public Message setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
		return this;
	}

	/**
	 * 重试的请求参数
	 * 
	 * @return
	 */
	public Message retryMessage() {
		Object deadQueue = null;
		if (properties.getHeaders() != null) {
			deadQueue = properties.getHeaders().get("x-first-death-queue");
		}
		String $deadQueue = null;
		if (deadQueue != null && StringUtils.isNotBlank($deadQueue = String.valueOf(deadQueue))
				&& !StringUtils.startsWith($deadQueue, com.swak.rabbit.Constants.retry_channel)) {
			return this.setOrigin($deadQueue);
		}
		return this;
	}

	/**
	 * 使用 build 之后消息才是完整的消息 如果没于设置id，则使用uuid来设置
	 * 
	 * @return
	 */
	public Message build() {
		if (properties == null) {
			id = StringUtils.isBlank(id) ? UUID.randomUUID().toString() : id;
			properties = new BasicProperties(null, Constants.DEFAULT_ENCODING.name(), null, deliveryMode, priority,
					null, null, expiration, id, null, null, null, null, null);
		}
		return this;
	}

	/**
	 * 创建一个空消息
	 */
	public static Message of() {
		return new Message();
	}
}