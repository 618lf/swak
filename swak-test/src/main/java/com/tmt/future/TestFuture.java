package com.tmt.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class TestFuture {

	/**
	 * 执行的一个任务
	 * @return
	 */
	private static int getJob() {
	    try {
	        Thread.sleep(1000);
	    } catch (InterruptedException e) {
	    }
	    return 50;
	}
	
	/**
	 * 测试 Stream ParallelStream 的性能
	 * ForkJoinPool.commonPool() 池大小 默认为cpu的核心数
	 * @param jobCount
	 * @return
	 */
	private static long testParallelStream(int jobCount) {
	    List<Supplier<Integer>> tasks = new ArrayList<>();
	    IntStream.rangeClosed(1, jobCount).forEach(value -> tasks.add(TestFuture::getJob));
	 
	    long start = System.currentTimeMillis();
	    tasks.parallelStream().map(Supplier::get).mapToInt(Integer::intValue).sum();
	    return System.currentTimeMillis() - start;
	}
	
	/**
	 * 测试 默认线程池的CompletableFuture 的性能
	 * ForkJoinPool.commonPool() 池大小 默认为cpu的核心数
	 * @param jobCount
	 * @return
	 */
	private static long testCompletableFutureDefaultExecutor(int jobCount) {
	    List<CompletableFuture<Integer>> tasks = new ArrayList<>();
	    IntStream.rangeClosed(1, jobCount).forEach(value -> tasks.add(CompletableFuture.supplyAsync(TestFuture::getJob)));
	 
	    long start = System.currentTimeMillis();
	    tasks.stream().map(CompletableFuture::join).mapToInt(Integer::intValue).sum();
	    return System.currentTimeMillis() - start;
	}
	
	/**
	 * 添加了自定义的线程池之后性能提高了很多
	 * @param jobCount
	 * @return
	 */
	private static long testCompletableFutureCustomExecutor(int jobCount) {
		ExecutorService executor = Executors.newFixedThreadPool(20);
	    List<CompletableFuture<Integer>> tasks = new ArrayList<>();
	    IntStream.rangeClosed(1, jobCount).forEach(value -> tasks.add(CompletableFuture.supplyAsync(TestFuture::getJob, executor)));
	 
	    long start = System.currentTimeMillis();
	    tasks.stream().map(CompletableFuture::join).mapToInt(Integer::intValue).sum();
	    return System.currentTimeMillis() - start;
	}
	
	public static void main(String[] args) {
		System.out.println(testParallelStream(20));
		System.out.println(testCompletableFutureDefaultExecutor(20));
		System.out.println(testCompletableFutureCustomExecutor(20));
	}
}
