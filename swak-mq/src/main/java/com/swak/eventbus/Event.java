package com.swak.eventbus;

import com.swak.incrementer.UUIdGenerator;
import com.swak.utils.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 系统定制的 event
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class Event {

	private String key;
	private Object message;

	@SuppressWarnings("unchecked")
	public <T> T getMessage() {
		return (T) message;
	}

	/**
	 * 重新设置消息
	 * 
	 * @param message
	 * @return
	 */
	public Event message(Object message) {
		Event event = new Event();
		event.setKey(this.key);
		event.setMessage(message);
		return event;
	}

	/**
	 * 构建消息
	 * 
	 * @return
	 */
	public Event build() {
		if (StringUtils.isBlank(this.key)) {
			this.key = UUIdGenerator.uuid();
		}
		return this;
	}

	/**
	 * 同类消息
	 * 
	 * @param me
	 * @return
	 */
	public boolean is(Event me) {
		if (me.getKey().equals(this.getKey())) {
			return true;
		}
		return false;
	}
}