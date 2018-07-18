package com.swak.executor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.util.Assert;

import com.swak.Constants;

import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.publisher.Operators;
import reactor.core.scheduler.Schedulers;

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

	/** 线城池： 设置、获取 */
	public static ConfigableExecutor executor;
	public static void executor(ConfigableExecutor executor) {
		Workers.executor = executor;
	}
	public static Executor executor(String name) {
		return executor.getExecutor(name);
	}
	// ----------------- 内置线程池执行代码 --------------------------
    public static <U> CompletableFuture<U> execute(Supplier<U> supplier) {
    	return CompletableFuture.supplyAsync(supplier, Workers.executor(Constants.default_pool));
    }
    public static <U> CompletableFuture<U> write(Supplier<U> supplier) {
    	return CompletableFuture.supplyAsync(supplier, Workers.executor(Constants.write_pool));
    }
    public static <U> CompletableFuture<U> read(Supplier<U> supplier) {
    	return CompletableFuture.supplyAsync(supplier, Workers.executor(Constants.read_pool));
    }
    public static <U> CompletableFuture<U> single(Supplier<U> supplier) {
    	return CompletableFuture.supplyAsync(supplier, Workers.executor(Constants.single_pool));
    }
	// ----------------- 异步执行代码(命名线程池) --------------------------
	/**
	 * 异步执行代码 -- 有返回值
	 * 
	 * @param supplier
	 * @return
	 */
	public static <T> CompletableFuture<T> future(String name, Supplier<T> supplier) {
		Assert.notNull(executor, "please init Worker Executor");
		return CompletableFuture.supplyAsync(supplier, executor.getExecutor(name));
	}
	
	/**
	 * 异步执行代码 -- 无返回值
	 * 
	 * @param supplier
	 * @return
	 */
	public static CompletableFuture<Void> future(String name, Runnable runnable) {
		Assert.notNull(executor, "please init Worker Executor");
		return CompletableFuture.runAsync(runnable, executor.getExecutor(name));
	}
	// ----------------- 异步执行代码(默认线程池) --------------------------
	/**
	 * 异步执行代码 -- 有返回值
	 * 
	 * @param supplier
	 * @return
	 */
	public static <T> CompletableFuture<T> future(Supplier<T> supplier) {
		Assert.notNull(executor, "please init Worker Executor");
		return CompletableFuture.supplyAsync(supplier, executor);
	}

	/**
	 * 异步执行代码 -- 无返回值
	 * 
	 * @param supplier
	 * @return
	 */
	public static CompletableFuture<Void> future(Runnable runnable) {
		Assert.notNull(executor, "please init Worker Executor");
		return CompletableFuture.runAsync(runnable, executor);
	}

	/**
	 * 异步执行代码 -- 有返回值
	 * 
	 * @param supplier
	 * @return
	 */
	@Deprecated
	public static <T> Mono<T> reactive(Supplier<T> supplier) {
		Assert.notNull(executor, "please init Worker Executor");
		return Mono.fromCompletionStage(CompletableFuture.supplyAsync(supplier, executor));
	}

	/**
	 * 异步执行代码 -- 有返回值
	 * 
	 * @param supplier
	 * @return
	 */
	@Deprecated
	public static Mono<Void> reactive(Runnable runnable) {
		Assert.notNull(executor, "please init Worker Executor");
		return Mono.fromCompletionStage(CompletableFuture.runAsync(runnable, executor));
	}

	/**
	 * 异步执行代码 -- 有返回值 通过sink
	 * 
	 * @param supplier
	 * @return
	 */
	@Deprecated
	public static <T, R> Mono<R> sink(CompletionStage<T> future, Function<T, R> func) {
		return Mono.create(sink -> {
			future.whenComplete((v, e) -> {
				try {
					if (e != null) {
						sink.error(e);
						return;
					}
					R r = func.apply(v);
					if (r != null) {
						sink.success(r);
					} else {
						sink.success();
					}
				} catch (Throwable e1) {
					sink.error(e1);
				}
			});
		});
	}

	/**
	 * 异步执行代码 -- 有返回值 通过sink
	 * 
	 * @param supplier
	 * @return
	 */
	@Deprecated
	public static <T> void sink(CompletionStage<T> future, MonoSink<T> sink) {
		future.whenComplete((v, e) -> {
			try {
				if (e != null) {
					sink.error(e);
				} else if (v != null) {
					sink.success(v);
				} else {
					sink.success();
				}
			} catch (Throwable e1) {
				Operators.onErrorDropped(e1, sink.currentContext());
				throw Exceptions.bubble(e1);
			}
		});
	}

	/**
	 * 异步执行代码 -- 有返回值 通过sink
	 * 
	 * @param supplier
	 * @return
	 */
	@Deprecated
	public static <T> Mono<T> sink(Supplier<T> supplier) {
		Assert.notNull(executor, "please init Worker Executor");
		return Mono.create((sink) -> {
			CompletableFuture.supplyAsync(supplier, executor).whenComplete((v, e) -> {
				try {
					if (e != null) {
						sink.error(e);
					} else if (v != null) {
						sink.success(v);
					} else {
						sink.success();
					}
				} catch (Throwable e1) {
					Operators.onErrorDropped(e1, sink.currentContext());
					throw Exceptions.bubble(e1);
				}
			});
		});
	}

	/**
	 * 异步执行代码 -- 无返回值 通过sink
	 * 
	 * @param supplier
	 * @return
	 */
	@Deprecated
	public static Mono<Void> sink(Runnable runnable) {
		Assert.notNull(executor, "please init Worker Executor");
		return Mono.create((sink) -> {
			CompletableFuture.runAsync(runnable, executor).whenComplete((v, e) -> {
				try {
					if (e != null) {
						sink.error(e);
					} else {
						sink.success();
					}
				} catch (Throwable e1) {
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
	 * 
	 * @param supplier
	 * @return
	 */
	@Deprecated
	public static <T> Mono<T> optional(Optional<T> optional) {
		return sink(() -> {
			return optional.get();
		});
	}

	/**
	 * 注意：代码是延迟执行的
	 * 
	 * 异步执行代码 -- 对 Stream 的支持, 注意只能返回一个值
	 * 
	 * @param supplier
	 * @return
	 */
	@Deprecated
	public static <T> Mono<T> stream(Stream<T> stream) {
		return sink(() -> {
			return stream.findFirst().get();
		});
	}

	/**
	 * 切换线程
	 * 
	 * @param mono
	 * @return
	 */
	@Deprecated
	public static <T> Mono<T> mono(Mono<T> mono) {
		return mono.subscribeOn(Schedulers.fromExecutor(executor));
	}
}