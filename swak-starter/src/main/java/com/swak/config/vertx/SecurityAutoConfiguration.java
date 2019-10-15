package com.swak.config.vertx;

import static com.swak.Application.APP_LOGGER;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.swak.Constants;
import com.swak.security.JwtAuthProvider;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.security.SecurityHandler;
import com.swak.vertx.security.SecurityManager;
import com.swak.vertx.security.SecurityUtils;
import com.swak.vertx.security.handler.Handler;
import com.swak.vertx.security.principal.PrincipalStrategy;
import com.swak.vertx.security.principal.TokenPrincipalStrategy;
import com.swak.vertx.transport.server.ReactiveServer;

/**
 * 安全配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@ConditionalOnBean({ SecurityConfigurationSupport.class })
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableSecurity", matchIfMissing = true)
public class SecurityAutoConfiguration {

	public SecurityAutoConfiguration() {
		APP_LOGGER.debug("Loading Security");
	}

	/**
	 * Auth 提供器
	 * 
	 * @param properties
	 * @return
	 */
	@Bean
	public JwtAuthProvider jwtAuth(VertxProperties properties) {
		JwtAuthProvider jwtAuth = new JwtAuthProvider(properties.getKeyStorePath(), properties.getKeyStorePass(),
				properties.getJwtTokenName());
		return jwtAuth;
	}

	/**
	 * 身份管理策略
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(PrincipalStrategy.class)
	public PrincipalStrategy principalStrategy(JwtAuthProvider jwtAuth) {
		return new TokenPrincipalStrategy(jwtAuth);
	}

	/**
	 * 安全管理
	 * 
	 * @param jwtAuthProvider
	 * @param securityConfig
	 * @return
	 */
	@Bean
	public SecurityManager securityManager(PrincipalStrategy principalStrategy,
			SecurityConfigurationSupport securityConfig) {
		SecurityManager securityManager = new SecurityManager(principalStrategy, securityConfig.getRealm());
		SecurityUtils.securityManager = securityManager;
		return securityManager;
	}

	/**
	 * 设置安全
	 * 
	 * @return
	 */
	@Bean
	public SecurityHandler securityFilter(SecurityManager securityManager,
			SecurityConfigurationSupport securityConfig) {
		SecurityHandler chainManager = new SecurityHandler(securityManager);

		// 可以配置新的filter
		Map<String, Handler> filters = securityConfig.getHandlers();
		if (!CollectionUtils.isEmpty(filters)) {
			for (Map.Entry<String, Handler> entry : filters.entrySet()) {
				String name = entry.getKey();
				Handler filter = entry.getValue();
				chainManager.addHandler(name, filter);
			}
		}

		// 构建filter chain path - filters
		Map<String, String> chains = securityConfig.getDefinitions();
		if (!CollectionUtils.isEmpty(chains)) {
			for (Map.Entry<String, String> entry : chains.entrySet()) {
				String url = entry.getKey();
				String chainDefinition = entry.getValue();
				chainManager.addDefinition(url, chainDefinition);
			}
		}
		return chainManager;
	}
}