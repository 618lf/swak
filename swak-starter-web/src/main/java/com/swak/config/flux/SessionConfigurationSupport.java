package com.swak.config.flux;

import static com.swak.Application.APP_LOGGER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.swak.cache.CacheManager;
import com.swak.reactivex.Session;
import com.swak.security.principal.support.CookiePrincipalStrategy;
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
	public SessionRepository<? extends Session> sessionRepository(ApplicationContext context,
		   @Autowired(required=false)CookiePrincipalStrategy principalStrategy) {
		APP_LOGGER.debug("Loading Session Manager");
		SessionRepository<? extends Session> repository = new CacheSessionRepository();
		SessionProvider.setRepository(repository);
		if (principalStrategy != null) {
			principalStrategy.setSessionRepository(repository);
		}
		return repository;
	}
}