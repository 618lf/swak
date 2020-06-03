package com.sample.tools.operation;

/**
 * 错误
 * @author lifeng
 */
public class OpsException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private final String message;
	
	public OpsException(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}
