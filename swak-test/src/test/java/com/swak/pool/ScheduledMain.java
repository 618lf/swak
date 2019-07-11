package com.swak.pool;

import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.swak.meters.MetricsFactory;
import com.swak.metrics.impl.CodahaleMetricsFactory;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.ScheduledContext;

public class ScheduledMain {

	public static void main(String[] args) {

		// 输出指标
		MetricRegistry registry = new MetricRegistry();
		Slf4jReporter.forRegistry(registry).outputTo(LoggerFactory.getLogger("com.swak.metrics"))
				.convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build()
				.start(10, TimeUnit.SECONDS);

		// 指标监控
		MetricsFactory metricsFactory = new CodahaleMetricsFactory(registry);
		ScheduledContext context = Contexts.createScheduledContext("Test.", 1, false, 2, TimeUnit.SECONDS);
		context.applyMetrics(metricsFactory);
		context.scheduleAtFixedRate(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}, 1, 2, TimeUnit.SECONDS);
	}
}
