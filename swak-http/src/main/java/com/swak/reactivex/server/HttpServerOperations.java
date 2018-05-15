package com.swak.reactivex.server;

import java.io.IOException;

import com.swak.common.exception.ErrorCode;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.handler.HttpHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

/**
 * HttpServer http 操作
 * @author lifeng
 */
public class HttpServerOperations extends HttpServerResponseOperation implements ServerOperations {

	private HttpHandler handler;
	private Channel channel;
	private FullHttpRequest request;
	
	private HttpServerOperations(HttpHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
	}

	@Override
	public void onError(Throwable e) {
		this.out(ErrorCode.OPERATE_FAILURE.toJson());
	}

	@Override
	public void onComplete() {
		this.out();
	}
	
	@Override
	protected Channel channel() {
		return channel;
	}

	/**
	 * 获得响应
	 * @return
	 */
	public HttpServerResponse getResponse() {
		return this;
	}
	
	/**
	 * 获得请求
	 */
	public HttpServerRequest getRequest() {
		return this;
	}
	
	/**
	 * 处理请求
	 * @param channel
	 * @param request
	 */
	public void handleStart() {
		try {
			this.initRequest(channel, request);
			this.handler.apply(this).subscribe(this);
		} catch (Exception e) {
			this.out(ErrorCode.OPERATE_FAILURE.toJson());
		}finally {
			ReferenceCountUtil.release(request);
		}
	}
	
	/**
	 * 设置通道
	 * @param channel
	 * @return
	 */
	public HttpServerOperations request(FullHttpRequest request) {
		this.request = request;
		return this;
	}
	
	/**
	 * 设置通道
	 * @param channel
	 * @return
	 */
	public HttpServerOperations channel(Channel channel) {
		this.channel = channel;
		return this;
	}
	
	/**
	 * 设置处理器
	 * @param handler
	 * @return
	 */
	public static HttpServerOperations apply(HttpHandler handler) {
		return new HttpServerOperations(handler);
	}
}