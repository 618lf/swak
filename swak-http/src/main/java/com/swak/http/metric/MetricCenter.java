package com.swak.http.metric;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.swak.http.server.HttpServer;

/**
 * 指标监控
 * 
 * @author lifeng
 */
public final class MetricCenter {

	private static MetricCenter metrics;
	private final MetricRegistry registry;
	
	// 系统级别的指标
	private final Counter activeCount;
	private final Counter requestCount;
	private final Meter qps;
	private final Histogram responseSizes;

	private MetricCenter() {
		this.registry = new MetricRegistry();
		this.activeCount = registry.counter("Channel Active");
		this.requestCount = registry.counter("Http request");
		this.qps = registry.meter("Requests per second");
		this.responseSizes = registry.histogram("Response sizes");
	}

	/**
	 * 得到系统唯一指标度量
	 * @return
	 */
	public static MetricCenter getMetrics() {
		if (metrics == null) {
			synchronized (MetricCenter.class) {
				if (metrics == null) {
					metrics = new MetricCenter();
				}
			}
		}
		return metrics;
	}

	/**
	 * 通道激活
	 */
	public static void channelActive() {
		if (metrics != null) {
			metrics.activeCount.inc();
		}
	}
	
	/**
	 * 通道闲置
	 */
	public static void channelInactive() {
		if (metrics != null) {
			metrics.activeCount.dec();
		}
	}
	
	/**
	 * 请求处理 -- 请求开始处理
	 */
	public static void requestHandler() {
		if (metrics != null) {
			metrics.requestCount.inc();
			metrics.qps.mark();
		}
	}
	
	/**
	 * 响应流量 -- 响应结束处理
	 */
	public static void responseSize(int size) {
		if (metrics != null) {
			metrics.responseSizes.update(size);
		}
	}
	
	/**
	 * 输出指标报表
	 */
	public static void report(ApplicationContext applicationContext) {
		
		// 创建指标
		getMetrics();
		
		// 注册稀有对象监控
		Arrays.stream(applicationContext.getBeanNamesForType(Reportable.class)).forEach(s -> {
			Reportable reportable = applicationContext.getBean(s, Reportable.class);
			reportable.report(getMetrics().registry);
		});
		
		// 启动生成报表
		Slf4jReporter reporter = Slf4jReporter.forRegistry(getMetrics().registry)
				.outputTo(LoggerFactory.getLogger(HttpServer.class))
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.build();
		reporter.start(1, TimeUnit.SECONDS);
	}
}