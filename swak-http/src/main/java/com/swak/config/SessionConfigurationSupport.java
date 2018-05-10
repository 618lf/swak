package com.swak.config;

import org.springframework.context.annotation.Bean;

import com.swak.security.session.Session;
import com.swak.security.session.SessionProvider;
import com.swak.security.session.SessionRepository;
import com.swak.security.session.support.CacheSessionRepository;

/**
 * Session 配置
 * @author lifeng
 */
public class SessionConfigurationSupport {
	
	/**
	 * Session 管理器
	 * @return
	 */
	@Bean
	public SessionRepository<? extends Session> sessionRepository() {
		SessionRepository<? extends Session> repository = new CacheSessionRepository();
		SessionProvider.setRepository(repository);
		return repository;
	}
}