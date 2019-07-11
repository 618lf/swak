package com.swak.metrics.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import com.swak.meters.MetricsFactory;
import com.swak.metrics.MetricsAutoConfiguration;
import com.swak.reactivex.threads.Contexts;

@Configuration
@ConditionalOnBean({ MetricsFactory.class })
@AutoConfigureAfter({ MetricsAutoConfiguration.class })
public class ContexteMetricsConfiguration {

	@Autowired
	public void contexteMetricsPostProcessor(MetricsFactory metricsFactory) {
		Contexts.setMetricsFactory(metricsFactory);
	}
}