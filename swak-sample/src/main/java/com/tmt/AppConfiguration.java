package com.tmt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.cache.collection.ReactiveMultiMap;
import com.swak.cache.collection.ReactiveMultiMapCache;
import com.swak.config.flux.SecurityConfigurationSupport;
import com.swak.reactivex.Session;
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
	public SessionRepository<? extends Session> sessionRepository() {
		ReactiveMultiMap<String, Object> _cache = new ReactiveMultiMapCache<Object>("SESSION:");
		return new CacheSessionRepository(_cache);
	}
	
	/**
	 * 使用 基于cookie的身份管理方式
	 * @return
	 */
	@Bean
	public PrincipalStrategy principalStrategy(SessionRepository<? extends Session> sessionRepository) {
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
		        .definition("/admin/**=user")
		        .definition("/=user")
				.realm(new SimpleRealm());
		return securityConfig;
	}
}