package com.swak.pool;

import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.swak.metrics.impl.PoolMetricsImpl;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.WorkerContext;
import com.swak.test.utils.MultiThreadTest;

/**
 * Pool 测试
 * 
 * @author lifeng
 */
public class PoolMain {

	public static void main(String[] args) {

		// 输出指标
		MetricRegistry registry = new MetricRegistry();
		Slf4jReporter.forRegistry(registry).outputTo(LoggerFactory.getLogger("com.swak.metrics"))
				.convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build()
				.start(10, TimeUnit.SECONDS);

		// 指标监控
		PoolMetricsImpl poolMetrics = new PoolMetricsImpl(registry, "Test", 1);
		WorkerContext context = Contexts.createWorkerContext("Test.", 1, false, 2, TimeUnit.SECONDS);
		context.setPoolMetrics(poolMetrics);

		// 用多线程来提交任务
		MultiThreadTest.run(() -> {
			while (true) {
				try {
					context.submit(() -> {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					});
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}, 10, "运行任务");
	}
}