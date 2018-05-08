package com.swak.common.eventbus;

/**
 * 消费的事件
 * 
 * @author lifeng
 */
public class Event {

	private Object message;

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
}