package com.swak.motan.reactor;

import java.util.concurrent.CompletableFuture;

import com.weibo.api.motan.rpc.ResponseFuture;

import reactor.core.publisher.Mono;

/**
 * 获取异步回调的结果, 并可以转化未 ComplatableFuture
 * 
 * @author lifeng
 */
public class Reactor {

	/**
	 * mono
	 * 
	 * 监控返回值，不会阻塞线程
	 * @param future
	 * @return
	 */
	public static Mono<Object> mono(ResponseFuture future) {
		return Mono.create(sink -> {
			future.addListener(f -> {
				if (f.isSuccess()) {
					sink.success(f.getValue());
				} else {
					sink.error(f.getException());
				}
			});
		});
	}
	
	/**
	 * CompletableFuture
	 * 
	 * 监控返回值，不会阻塞线程
	 * @param future
	 * @return
	 */
	public static CompletableFuture<Object> future(ResponseFuture future) {
		return mono(future).toFuture();
	}
}