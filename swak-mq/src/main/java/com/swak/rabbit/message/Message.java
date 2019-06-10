package com.swak.rabbit.message;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.swak.Constants;
import com.swak.rabbit.AmqpException;
import com.swak.serializer.SerializationUtils;

import io.netty.util.internal.StringUtil;

/**
 * 基础的消息 (不可序列化，仅仅作为发送消息的载体)
 * 
 * @author lifeng
 */
public class Message {

	private BasicProperties properties;
	private byte[] payload; // 消息的内容

	// 消息可以设置的参数
	private String id; // 设置到 messageId 中
	private Integer deliveryMode = 2; // 发布模式1(不持久化)， 2(持久化) 默认是持久化的消息，可以更改
	private Integer priority; // 消息的优先级 0 - 9
	private String expiration; // 消息的 ttl (s)

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

	/**
	 * 使用 build 之后消息才是完整的消息
	 * 
	 * @return
	 */
	public Message build() {
		properties = new BasicProperties(null, Constants.DEFAULT_ENCODING.name(), null, deliveryMode, priority, null,
				null, expiration, id, null, null, null, null, null);
		return this;
	}

	/**
	 * 创建一个空消息
	 */
	public static Message builder() {
		return new Message();
	}
}