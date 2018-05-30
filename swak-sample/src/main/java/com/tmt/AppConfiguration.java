package com.tmt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.flux.SecurityConfigurationSupport;
import com.tmt.consumer.UpdateEventConsumer;
import com.tmt.filter.BusinessPoolFilter;
import com.tmt.realm.SimpleRealm;

/**
 * 项目配置
 * @author lifeng
 */
@Configuration
public class AppConfiguration {
	
	/**
	 * 注册消费者
	 * @return
	 */
	@Bean
	public UpdateEventConsumer updateEventConsumer() {
		return new UpdateEventConsumer();
	}

	/**
	 * 业务线程池 -- 需要线程隔离， 而不是无目的的切换线程
	 * @return
	 */
	public BusinessPoolFilter businessPoolFilter() {
		return new BusinessPoolFilter();
	}
	
	/**
	 * 配置安全管理
	 * 
	 * @return
	 */
	@Bean
	public SecurityConfigurationSupport securityConfigurationSupport() {
		SecurityConfigurationSupport securityConfig = new SecurityConfigurationSupport();
		securityConfig.definition("/admin/hello/**=anon")
		        .definition("/admin/**=user")
		        .definition("/=user")
				.realm(new SimpleRealm());
		return securityConfig;
	}
}