package com.swak.rocketmq.exception;

/**
 * 消息错误异常
 * 
 * @author lifeng
 */
public class MessagingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MessagingException(String description) {
		super(description);
	}
	
	public MessagingException(String description, Throwable cause) {
		super(description, cause);
	}
}
