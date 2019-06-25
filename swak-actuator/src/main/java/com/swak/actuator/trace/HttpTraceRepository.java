package com.swak.actuator.trace;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;

/**
 * HttpTrace 存储服务
 * @author lifeng
 */
public interface HttpTraceRepository {

	/**
	 * 收到请求
	 * @param request
	 * @return
	 */
	default HttpTrace receivedRequest(HttpServerRequest request) {
		return new HttpTrace(request);
	}
	
	/**
	 * 输出响应
	 * @param trace
	 * @param response
	 */
	default void sendingResponse(HttpTrace trace, HttpServerResponse response) {
		trace.setResponse(new HttpTrace.Response(response));
		this.storage(trace);
	}
	
	/**
	 * 存储
	 * @param trace
	 */
	void storage(HttpTrace trace);
}