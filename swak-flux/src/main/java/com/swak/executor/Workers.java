package com.swak.executor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.swak.reactivex.transport.resources.EventLoops;

/**
 * 
 * 全局： 用来执行耗时的操作。例如：数据库、文件IO、耗时计算（生成图片）
 * 
 * https://yq.aliyun.com/articles/591627
 * 
 * 通过线城池 executor来异步执行任务，提供不同的获取结果的方式
 * 
 * 延迟： stream 、mono 延迟执行，只有最后终端操作时才会触发整个执行链 future 、optional 立即执行
 * 
 * 可重用： future、optional、mono 可重用 stream 不可重用
 * 
 * 异步： future、mono 异步执行 stream、optional 不可异步
 * 
 * 推模式还是拉模式： Stream 、Optional 是拉模式的 Future、mono 推模式
 * 
 * 重要性： Mono、Optional、future 是可以重用的 意味着：可以多次获取结果，而不会重复计算
 * 
 * 
 * 注意： sink 中返回的值不能为 null， 不然事件发送不出去
 * 
 * @author lifeng
 */
public class Workers {

	// ----------------- 异步执行代码(命名线程池) --------------------------
	/**
	 * 异步执行代码 -- 有返回值
	 * 
	 * @param supplier
	 * @return
	 */
	public static <T> CompletableFuture<T> future(String name, Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, EventLoops.fetch(name));
	}
	
	/**
	 * 异步执行代码 -- 无返回值
	 * 
	 * @param supplier
	 * @return
	 */
	public static CompletableFuture<Void> future(String name, Runnable runnable) {
		return CompletableFuture.runAsync(runnable, EventLoops.fetch(name));
	}
}