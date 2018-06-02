package com.swak.reactivex.server;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;

public interface ServerOperations extends Subscriber<Void>{

	/**
	 * 请求所有数据
	 */
	default void onSubscribe(Subscription s) {
	    s.request(Long.MAX_VALUE);	
	}
	
	/**
	 * 不需要
	 */
	default void onNext(Void t) {}
	
	/**
	 * 程序执行错误
	 */
	default void onError(Throwable e) {
		this.getResponse().error().buffer(e);
		this.onComplete();
	}
	
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
