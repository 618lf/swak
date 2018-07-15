package com.swak.actuator.config.pool;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;

import com.swak.actuator.pool.PoolAllocatorEndpoint;
import com.swak.actuator.pool.ThreadPoolEndpoint;
import com.swak.utils.Maps;

/**
 * 系统 Pool 相关
 * 
 * @author lifeng
 */
@Configuration
public class PoolEndpointAutoConfiguration {

	/**
	 * 通过 endpoint 来展示指标
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public PoolAllocatorEndpoint poolAllocatorEndpoint() {
		return new PoolAllocatorEndpoint();
	}

	/**
	 * 通过 mbean 来展示指标
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public MBeanExporter pooledstatsMBeanExporter(PoolAllocatorEndpoint endpoint) {
		MBeanExporter mBeanExporter = new MBeanExporter();
		Map<String, Object> beans = Maps.newHashMap();
		beans.put("com.swak:name=pooledstats", endpoint);
		mBeanExporter.setBeans(beans);
		return mBeanExporter;
	}
	
	/**
	 * 通过 endpoint 来展示指标
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public ThreadPoolEndpoint threadPoolEndpoint() {
		return new ThreadPoolEndpoint();
	}
}