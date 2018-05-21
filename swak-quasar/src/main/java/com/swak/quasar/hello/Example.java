package com.swak.quasar.hello;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import co.paralleluniverse.fibers.FiberExecutorScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.strands.Strand;

/**
 * 这一版的测试先做到这个，可以直接使用喇嘛打的方式来执行
 * 不知道为啥 m run 会重复执行。感觉没有kotlin 的好用
 * @author lifeng
 */
public class Example {
	
	private final Executor executor = Executors.newFixedThreadPool(20);
	private final FiberScheduler scheduler = new FiberExecutorScheduler("rpcHandler", executor);
	
	public void run(Runnable run) {
		scheduler.<Void> newFiber(() -> {
			run.run();
			Strand.sleep(1000);
			return null;
		}).start();
	}
	
	public static void main(String[] args) {
		Example example = new Example();
		example.run(() ->{
		   System.out.println("m run");
		});
	}
}