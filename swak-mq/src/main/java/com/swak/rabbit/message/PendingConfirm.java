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
}