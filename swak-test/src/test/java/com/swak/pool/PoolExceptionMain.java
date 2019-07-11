package com.swak.pool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.WorkerContext;

public class PoolExceptionMain {

	public static void main(String[] args) {
		WorkerContext context = Contexts.createWorkerContext("Test.", 1, false, 2, TimeUnit.SECONDS, 1, new DiscardPolicy());
		context.execute(() -> {
			System.out.println("启动线程");
		});
		AtomicInteger count = new AtomicInteger(0);
		for (int i = 0; i < 10; i++) {
			
			/**
			 * 提交到一个队列已满的线程池，任务会无法运行。
			 * @see AbortPolicy 至少会抛出异常
			 * @see DiscardPolicy 更加扯淡，不會告訴你
			 */
			CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				return true;
			}, context);

			/**
			 * 这种方式通过捕获异常可以让系统运行下去
			 */
			// CompletableFuture<Boolean> future = new CompletableFuture<>();
			// try {
			// context.execute(() -> {
			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			// }
			// future.complete(true);
			// });
			// } catch (Exception e) {
			// future.complete(true);
			// }
			future.whenComplete((r, v) -> {
				System.out.println(count.incrementAndGet());
			});
			System.out.println("i:" + i);
		}
	}
}
