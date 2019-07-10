package com.swak.lock;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 异步非阻塞锁
 * 
 * @author lifeng
 */
public interface AsyncLock extends Lock {

	/**
	 * 返回异步对象，执行完成之后回调继续执行，注意切线程
	 * 
	 * @param handler
	 * @return
	 */
	<T> CompletableFuture<T> execute(Supplier<T> handler);
}