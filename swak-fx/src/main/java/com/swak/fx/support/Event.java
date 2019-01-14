package com.swak.fx.support;

/**
 * 系统定制的 event
 * 
 * @author lifeng
 */
public class Event {

	public static Event CLOSE = new Event("close");
	public static Event EXIT = new Event("exit");
	public static Event DOWNLOAD = new Event("DOWNLOAD");

	private String key;
	private Object message;

	public Event() {
	}

	public Event(String key) {
		this.key = key;
	}

	public Event(String key, Object message) {
		this.key = key;
		this.message = message;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
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