package com.swak.actuator.config.pool;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.pool.PoolAllocatorEndpoint;

/**
 * 系统 Pool 相关
 * @author lifeng
 */
@Configuration
public class PoolEndpointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public PoolAllocatorEndpoint poolAllocatorEndpoint() {
		return new PoolAllocatorEndpoint();
	}
}