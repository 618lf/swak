package com.swak.flux.transport.server;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.NestedRuntimeException;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 响应异常
 * 
 * @author lifeng
 */
public class ResponseStatusException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;
	private final HttpResponseStatus status;
	private final String reason;
	


	/**
	 * Constructor with a response status.
	 * @param status the HTTP status (required)
	 */
	public ResponseStatusException(HttpResponseStatus status) {
		this(status, null, null);
	}

	/**
	 * Constructor with a response status and a reason to add to the exception
	 * message as explanation.
	 * @param status the HTTP status (required)
	 * @param reason the associated reason (optional)
	 */
	public ResponseStatusException(HttpResponseStatus status, String reason) {
		this(status, reason, null);
	}

	/**
	 * Constructor with a response status and a reason to add to the exception
	 * message as explanation, as well as a nested exception.
	 * @param status the HTTP status (required)
	 * @param reason the associated reason (optional)
	 * @param cause a nested exception (optional)
	 */
	public ResponseStatusException(HttpResponseStatus status, String reason, Throwable cause) {
		super(null, cause);
		this.status = status;
		this.reason = reason;
	}


	/**
	 * The HTTP status that fits the exception (never {@code null}).
	 */
	public HttpResponseStatus getStatus() {
		return this.status;
	}

	/**
	 * The reason explaining the exception (potentially {@code null} or empty).
	 */
	public String getReason() {
		return this.reason;
	}

	@Override
	public String getMessage() {
		String msg = this.status + (this.reason != null ? " \"" + this.reason + "\"" : "");
		return NestedExceptionUtils.buildMessage(msg, getCause());
	}
}
