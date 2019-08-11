package com.swak.metrics;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.codahale.metrics.ScheduledReporter;
import com.swak.meters.MetricsFactory;
import com.swak.metrics.annotation.MetricBinder;
import com.swak.utils.ConcurrentHashSet;

/**
 * 注册指标监听
 * 
 * @author lifeng
 */
public class MetricRegistryPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

	private MetricsFactory metricsFactory;
	private ScheduledReporter reporter;
	private final ConcurrentHashSet<MetricBinder> binders = new ConcurrentHashSet<>();

	public MetricRegistryPostProcessor(MetricsFactory metricsFactory) {
		this.metricsFactory = metricsFactory;
	}

	/**
	 * 收集关键指标統計
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String arg1) throws BeansException {
		if (bean instanceof ScheduledReporter) {
			this.reporter = (ScheduledReporter) bean;
		} else if (bean instanceof MetricBinder) {
			binders.add((MetricBinder) bean);
		}
		return bean;
	}

	/**
	 * 系统起来之后收集指标，上报
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (metricsFactory != null) {
			binders.stream().forEach(binder -> {
				binder.bindTo(metricsFactory.metricRegistry());
			});
		}
		if (reporter != null) {
			reporter.start(1, 10, TimeUnit.SECONDS);
		}
	}
}