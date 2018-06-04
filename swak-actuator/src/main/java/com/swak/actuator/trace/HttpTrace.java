package com.swak.actuator.trace;

import java.time.Instant;
import java.util.Map;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;

/**
 * http trace
 * @author lifeng
 */
public class HttpTrace {
	
	private final Instant timestamp = Instant.now();
	private Long timeTaken;
	private final Request request;
	private Response response;
	
	public Instant getTimestamp() {
		return this.timestamp;
	}
	
	HttpTrace(HttpServerRequest request) {
		this.request = new Request(request);
	}
	
	public Long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(Long timeTaken) {
		this.timeTaken = timeTaken;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public Request getRequest() {
		return request;
	}

	/**
	 * Trace of an HTTP request.
	 */
	public static final class Request {
		private final String method;
		private final String uri;
		private final String remoteAddress;
		private final Map<String, String> headers;
		private Request(HttpServerRequest request) {
			this.method = request.getRequestMethod().name();
			this.uri = request.getRequestURI();
			this.remoteAddress = request.getRemoteAddress();
			this.headers = request.getRequestHeaders();
		}
		public String getMethod() {
			return method;
		}
		public String getUri() {
			return uri;
		}
		public String getRemoteAddress() {
			return remoteAddress;
		}
		public Map<String, String> getHeaders() {
			return headers;
		}
	}
	
	/**
	 * Trace of an HTTP response.
	 */
	public static final class Response {
		private final int status;
		private final Map<String, String> headers;
		Response(HttpServerResponse response) {
			this.status = response.getStatus().code();
			this.headers = response.getHeaders();
		}
		public int getStatus() {
			return status;
		}
		public Map<String, String> getHeaders() {
			return headers;
		}
	}
}