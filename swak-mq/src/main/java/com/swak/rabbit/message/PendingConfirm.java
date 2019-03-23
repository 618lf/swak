package com.swak.rabbit.message;

import java.io.Serializable;

/**
 * 待确认的数据
 * 
 * @author lifeng
 */
public class PendingConfirm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private long timestamp;
	private String cause;
	private int retryTimes;

	// 消息重试时需要设置的消息
	private String exchange;
	private String routingKey;
	private Integer deliveryMode = 2; // 发布模式1(不持久化)， 2(持久化) 默认是持久化的消息，可以更改
	private Integer priority; // 消息的优先级 0 - 9
	private String expiration; // 消息的 ttl
	private byte[] payload; // 消息的内容

	public PendingConfirm() {
	}

	public PendingConfirm(String id) {
		this.id = id;
		this.timestamp = System.currentTimeMillis();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getExchange() {
		return exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public Integer getDeliveryMode() {
		return deliveryMode;
	}

	public Integer getPriority() {
		return priority;
	}

	public String getExpiration() {
		return expiration;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public void setDeliveryMode(Integer deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}
}