package com.swak.executor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.util.Assert;

import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;

/**
 * 通过线城池 executor来异步执行任务，提供不同的获取结果的方式
 * @author lifeng
 */
public class Workers {
	
	/**
	 * 线城池
	 */
	private static Executor executor;
	public static void executor(Executor executor) {
		Workers.executor =executor;
	}
	public static Executor executor() {
		return executor;
	}
	
	/**
	 * 异步执行代码 -- 有返回值
	 * @param supplier
	 * @return
	 */
	public static <T> CompletableFuture<T> future(Supplier<T> supplier) {
		Assert.notNull(executor, "please init Worker Executor");
		return CompletableFuture.supplyAsync(supplier, executor);
	}
	
	/**
	 * 异步执行代码 -- 无返回值
	 * @param supplier
	 * @return
	 */
	public static CompletableFuture<Void> future(Runnable runnable) {
		Assert.notNull(executor, "please init Worker Executor");
		return CompletableFuture.runAsync(runnable, executor);
	}
	
	/**
	 * 异步执行代码 -- 有返回值
	 * @param supplier
	 * @return
	 */
	public static <T> Mono<T> reactive(Supplier<T> supplier) {
		Assert.notNull(executor, "please init Worker Executor");
		return Mono.fromCompletionStage(CompletableFuture.supplyAsync(supplier, executor));
	}
	
	/**
	 * 异步执行代码 -- 有返回值
	 * @param supplier
	 * @return
	 */
	public static Mono<Void> reactive(Runnable runnable) {
		Assert.notNull(executor, "please init Worker Executor");
		return Mono.fromCompletionStage(CompletableFuture.runAsync(runnable, executor));
	}
	
	/**
	 * 异步执行代码 -- 有返回值 通过sink
	 * @param supplier
	 * @return
	 */
	public static <T> Mono<T> sink(Supplier<T> supplier) {
		Assert.notNull(executor, "please init Worker Executor");
		return Mono.create((sink) -> {
			CompletableFuture.supplyAsync(supplier, executor).whenComplete((v, e) ->{
				 try {
	                if (e != null) {
	                	sink.error(e);
	                }
	                else if (v != null) {
	                    sink.success(v);
	                }
	                else {
	                    sink.success();
	                }
	            }
	            catch (Throwable e1) {
	                Operators.onErrorDropped(e1, sink.currentContext());
	                throw Exceptions.bubble(e1);
	            }
			});
		});
	}
	
	/**
	 * 异步执行代码 -- 无返回值 通过sink
	 * @param supplier
	 * @return
	 */
	public static Mono<Void> sink(Runnable runnable) {
		Assert.notNull(executor, "please init Worker Executor");
		return Mono.create((sink) -> {
			CompletableFuture.runAsync(runnable, executor).whenComplete((v, e) ->{
				 try {
	                if (e != null) {
	                	sink.error(e);
	                }
	                else {
	                    sink.success();
	                }
	            }
	            catch (Throwable e1) {
	                Operators.onErrorDropped(e1, sink.currentContext());
	                throw Exceptions.bubble(e1);
	            }
			});
		});
	}
	
	/**
	 * 注意： 代码不是延迟执行的，所以不要在构建 optional 中执行耗时的操作
	 * 
	 * 异步执行代码 -- 对 optional 的支持
	 * @param supplier
	 * @return
	 */
	public static <T> Mono<T> optional(Optional<T> optional) {
		return sink(() -> {
			return optional.get();
		}) ;
	}
	
	/**
	 * 注意：代码是延迟执行的
	 * 
	 * 异步执行代码 -- 对 Stream 的支持, 注意只能返回一个值
	 * @param supplier
	 * @return
	 */
	public static <T> Mono<T> stream(Stream<T> stream) {
		return sink(() -> {
			return stream.findFirst().get();
		}) ;
	}
}