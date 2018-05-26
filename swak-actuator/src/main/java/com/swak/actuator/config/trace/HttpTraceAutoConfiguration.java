package com.swak.actuator.config.trace;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.actuator.trace.HttpTraceRepository;
import com.swak.actuator.trace.HttpTraceWebFilter;
import com.swak.actuator.trace.LoggerHttpTraceRepository;

/**
 * 默认开启 Http Trace
 * @author lifeng
 */
@Configuration
@ConditionalOnProperty(prefix = Constants.ACTUATOR_TRACE, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(HttpTraceProperties.class)
public class HttpTraceAutoConfiguration {
	
	/**
	 * 默认开启日志的方式存储 Trace
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(prefix = Constants.ACTUATOR_TRACE, name = "storageMethod", havingValue="LOGGER", matchIfMissing = true)
	public LoggerHttpTraceRepository traceRepository(HttpTraceProperties httpTraceProperties) {
		return new LoggerHttpTraceRepository(httpTraceProperties);
	}
	
	/**
	 * 开启跟踪
	 * @param repository
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(HttpTraceRepository.class)
	public HttpTraceWebFilter httpTraceFilter(HttpTraceRepository repository) {
		return new HttpTraceWebFilter(repository);
	}
}
