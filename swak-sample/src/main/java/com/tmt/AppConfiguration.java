package com.tmt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.SecurityConfigurationSupport;
import com.tmt.filter.BusinessPoolFilter;
import com.tmt.realm.SimpleRealm;

/**
 * 项目配置
 * @author lifeng
 */
@Configuration
public class AppConfiguration {

	/**
	 * 业务线程池
	 * @return
	 */
	@Bean
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