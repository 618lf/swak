package com.tmt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.cache.collection.AsyncMultiMap;
import com.swak.cache.collection.AsyncMultiMapCache;
import com.swak.config.flux.SecurityConfigurationSupport;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.principal.SessionRepository;
import com.swak.security.principal.support.CacheSessionRepository;
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
	public SessionRepository sessionRepository() {
		AsyncMultiMap<String, Object> _cache = new AsyncMultiMapCache<Object>("SESSION");
		return new CacheSessionRepository(_cache);
	}
	
	/**
	 * 使用 基于cookie的身份管理方式
	 * @return
	 */
	@Bean
	public PrincipalStrategy principalStrategy(SessionRepository sessionRepository) {
		return new CookiePrincipalStrategy("SESSION", sessionRepository);
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
		        .definition("/admin/login=authc")
		        .definition("/admin/logout=logout")
		        .definition("/admin/user/get=user")
		        .definition("/admin/user/admin=user,roles[admin]")
		        .definition("/admin/user/system=user,roles[system]")
		        .definition("/admin/**=user")
		        .definition("/admin/**=user")
		        .definition("/=user")
				.realm(new SimpleRealm());
		return securityConfig;
	}
}