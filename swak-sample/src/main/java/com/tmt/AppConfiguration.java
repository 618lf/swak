package com.tmt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.flux.SecurityConfigurationSupport;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.principal.support.CookiePrincipalStrategy;
import com.tmt.consumer.UpdateEventConsumer;
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
	 * 使用 基于cookie的身份管理方式
	 * @return
	 */
	@Bean
	public PrincipalStrategy principalStrategy() {
		return new CookiePrincipalStrategy();
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