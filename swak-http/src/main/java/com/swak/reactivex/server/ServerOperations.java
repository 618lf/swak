package com.swak.reactivex.server;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public interface ServerOperations extends Subscriber<Void>{

	/**
	 * 请求所有数据
	 */
	default void onSubscribe(Subscription s) {
	    s.request(Long.MAX_VALUE);	
	}
	
	/**
	 * 下一步处理
	 */
	default void onNext(Void t) {}

	/**
	 * 获得响应
	 * @return
	 */
	HttpServerResponse getResponse();
	
	/**
	 * 获得请求
	 */
	HttpServerRequest getRequest();
}
