package com.swak.common.eventbus;

/**
 * 消费的事件
 * 
 * @author lifeng
 */
public class Event {

	/*
	 * 具体的消息
	 */
	private Object message;
	
	/*
	 * 消息类型
	 */
	private int type;

	public Event() {}
	public Event(Object message) {
		this.message = message;
	}

	@SuppressWarnings("unchecked")
	public <T> T getMessage() {
		return (T) message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * 设置类型
	 * @param type
	 * @return
	 */
	public Event type(int type) {
		this.type = type;
		return this;
	}
	
	/**
	 * 设置message
	 * @param message
	 * @return
	 */
	public static Event msg(Object message) {
		return new Event(message);
	}
}