package com.swak.reactivex.server;

import java.io.IOException;

import com.swak.common.exception.ErrorCode;
import com.swak.reactivex.handler.HttpHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * HttpServer http 操作
 * @author lifeng
 */
public class HttpServerOptions extends HttpServerResponse implements Observer<Void>{

	private HttpHandler handler;
	private HttpServerOptions(HttpHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
	}
	
	@Override
	public void onSubscribe(Disposable d) {
		
	}

	@Override
	public void onNext(Void t) {
		
	}

	@Override
	public void onError(Throwable e) {
		this.out(ErrorCode.OPERATE_FAILURE.toJson());
	}

	@Override
	public void onComplete() {
		this.out();
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
	public HttpServerResponse getRequest() {
		return this;
	}
	
	/**
	 * 处理请求
	 * @param channel
	 * @param request
	 */
	public void handle(ChannelHandlerContext channel, FullHttpRequest request) {
		this.initRequest(channel, request);
		this.handler.apply(this).subscribe(this);
	}
	
	/**
	 * 设置处理器
	 * @param handler
	 * @return
	 */
	public static HttpServerOptions apply(HttpHandler handler) {
		return new HttpServerOptions(handler);
	}
}