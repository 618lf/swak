package com.swak.actuator.config.dump;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.dump.HeapDumpWebEndpoint;
import com.swak.actuator.dump.ThreadDumpEndpoint;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link HeapDumpWebEndpoint}.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
@Configuration
public class DumpEndpointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ThreadDumpEndpoint dumpEndpoint() {
		return new ThreadDumpEndpoint();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public HeapDumpWebEndpoint heapDumpWebEndpoint() {
		return new HeapDumpWebEndpoint();
	}
}