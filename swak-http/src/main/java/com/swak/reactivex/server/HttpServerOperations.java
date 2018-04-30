package com.swak.reactivex.server;

import java.io.IOException;

import com.swak.common.exception.ErrorCode;
import com.swak.reactivex.handler.HttpHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * HttpServer http 操作
 * @author lifeng
 */
public class HttpServerOperations extends HttpServerResponse implements Observer<Void>{

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
	
	@Override
	protected Channel channel() {
		return channel;
	}

	@Override
	protected FullHttpRequest request() {
		return request;
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
	public void handleStart() {
		try {
			this.initRequest(channel, request);
			this.handler.apply(this).subscribe(this);
		} catch(Exception e) {
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