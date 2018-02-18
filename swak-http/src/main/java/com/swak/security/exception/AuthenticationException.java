package com.swak.security.exception;

import com.swak.http.ErrorCode;

/**
 * 登录错误的异常
 * @author lifeng
 */
public class AuthenticationException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	protected ErrorCode errorCode;
	
	/**
	 * 返回异常码
	 * @return
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}
	
	/**
	 * 返回异常消息
	 */
	public String getMessage() {
		return this.buildMessage(super.getMessage(), getCause());
	}

	/**
	 * 构建异常消息
	 * @param message
	 * @param cause
	 * @return
	 */
	public String buildMessage(String message, Throwable cause) {
		if (cause != null) {
			StringBuffer buf = new StringBuffer();
			if (message != null) {
				buf.append(message).append("; ");
			}
			buf.append("nested exception is ").append(cause);
			return buf.toString();
		} else {
			return message;
		}
	}
	
	/**
	 * 构建异常，一定需要异常码
	 * @param code
	 * @param msg
	 */
	public AuthenticationException(ErrorCode code, String msg) {
		super(msg);
		this.errorCode = code;
	}
	
	/**
	 * 通过异常吗构架消息
	 * @param code
	 */
	public AuthenticationException(ErrorCode code) {
		super(code.getMsg());
		this.errorCode = code;
	}
}