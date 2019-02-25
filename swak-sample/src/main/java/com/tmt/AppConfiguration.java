package com.tmt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.flux.SecurityConfigurationSupport;
import com.tmt.consumer.UpdateEventConsumer;
import com.tmt.realm.SimpleRealm;

/**
 * 项目配置
 * 
 * @author lifeng
 */
@Configuration
public class AppConfiguration {

	/**
	 * 注册消费者
	 * 
	 * @return
	 */
	@Bean
	public UpdateEventConsumer updateEventConsumer() {
		return new UpdateEventConsumer();
	}

	/**
	 * 配置安全管理
	 * 
	 * @return
	 */
	@Bean
	public SecurityConfigurationSupport securityConfigurationSupport() {
		SecurityConfigurationSupport securityConfig = new SecurityConfigurationSupport();
		securityConfig.definition("/admin/hello/**=anon").definition("/admin/motan/**=anon").definition("/admin/login=authc")
				.definition("/admin/logout=logout").definition("/admin/user/get=user")
				.definition("/admin/user/admin=user,roles[admin]").definition("/admin/user/system=user,roles[system]")
				.definition("/admin/**=user").definition("/admin/**=user").definition("/**=anon")
				.realm(new SimpleRealm());
		return securityConfig;
	}
}