package com.swak.reactivex.server;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.handler.HttpHandler;
import com.swak.utils.IOUtils;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

/**
 * HttpServer http 操作
 * @author lifeng
 */
public class HttpServerOperations extends HttpServerResponseOperation implements ServerOperations {

	private HttpHandler handler;
	private String serverName;
	private Channel channel;
	private FullHttpRequest request;
	
	private HttpServerOperations(HttpHandler handler) {
		this.handler = handler;
	}
	
	/**
	 * 直接输出响应
	 */
	@Override
	public void onComplete() {
        try {
        	this.out();
		} finally {
			IOUtils.closeQuietly(this);
			ReferenceCountUtil.release(request);
		}
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
	 * 返回提供服务的名称
	 */
	public String getServerName() {
		return serverName;
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
			this.onError(e);
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
	 * 提供服务的名称
	 * @param channel
	 * @return
	 */
	public HttpServerOperations server(String serverName) {
		this.serverName = serverName;
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