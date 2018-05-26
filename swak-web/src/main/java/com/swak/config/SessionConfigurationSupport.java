package com.swak.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.swak.cache.CacheManager;
import com.swak.reactivex.Session;
import com.swak.security.session.SessionProvider;
import com.swak.security.session.SessionRepository;
import com.swak.security.session.support.CacheSessionRepository;

/**
 * Session 配置
 * @author lifeng
 */
public class SessionConfigurationSupport {
	
	/**
	 * Session 管理器 -- 基于缓存的 session 管理
	 * @return
	 */
	@Bean
	@ConditionalOnBean(CacheManager.class)
	@ConditionalOnMissingBean(SessionRepository.class)
	public SessionRepository<? extends Session> sessionRepository() {
		SessionRepository<? extends Session> repository = new CacheSessionRepository();
		SessionProvider.setRepository(repository);
		return repository;
	}
}