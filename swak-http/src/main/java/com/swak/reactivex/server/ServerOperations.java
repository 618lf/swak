package com.swak.reactivex.server;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public interface ServerOperations extends Observer<Void>{

	/**
	 * 开始执行
	 */
	default void onSubscribe(Disposable d) {}

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
	HttpServerResponse getRequest();
}
