package com.swak.actuator.config.metrics;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.config.MeterFilter;

public class MeterRegistryPostProcessor implements BeanPostProcessor {

	private final ApplicationContext context;

	private volatile MeterRegistryConfigurer configurer;

	public MeterRegistryPostProcessor(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof MeterRegistry) {
			getConfigurer().configure((MeterRegistry) bean);
		}
		return bean;
	}

	@SuppressWarnings("unchecked")
	private MeterRegistryConfigurer getConfigurer() {
		if (this.configurer == null) {
			this.configurer = new MeterRegistryConfigurer(beansOfType(MeterBinder.class),
					beansOfType(MeterFilter.class),
					(Collection<MeterRegistryCustomizer<?>>) (Object) beansOfType(
							MeterRegistryCustomizer.class),
					this.context.getBean(MetricsProperties.class).isUseGlobalRegistry());
		}
		return this.configurer;
	}

	private <T> Collection<T> beansOfType(Class<T> type) {
		return this.context.getBeansOfType(type).values();
	}
}
