package com.swak.lock;

import java.util.concurrent.CompletableFuture;

import reactor.core.publisher.MonoSink;

public interface LockEntity<T> {

	/**
	 * 资源上执行的操作
	 * @author root
	 */
	CompletableFuture<T> doHandle();
	
	/**
	 * 对应的资源，获取
	 * @return
	 */
	String getResource();
	
	/**
	 * 异步通知对象
	 * @param sink
	 */
	LockEntity<T> sink(MonoSink<T> sink);
	
	/**
	 * 异步通知对象
	 * @param sink
	 */
	MonoSink<T> sink(); 
}
