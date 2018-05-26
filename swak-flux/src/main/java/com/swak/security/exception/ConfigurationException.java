package com.swak.security.exception;

public class ConfigurationException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public ConfigurationException(String msg) {
		super(msg);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	public ConfigurationException(String msg, Throwable cause) {
		super(msg, cause);
	}
}