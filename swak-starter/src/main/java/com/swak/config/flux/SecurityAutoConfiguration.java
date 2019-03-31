package com.swak.config.flux;

import static com.swak.Application.APP_LOGGER;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.swak.Constants;
import com.swak.flux.handler.WebFilter;
import com.swak.flux.security.SecurityFilter;
import com.swak.flux.security.SecurityUtils;
import com.swak.flux.security.mgt.FilterChainManager;
import com.swak.flux.security.mgt.SecurityManager;
import com.swak.flux.security.mgt.support.DefaultFilterChainManager;
import com.swak.flux.security.mgt.support.DefaultSecurityManager;
import com.swak.flux.security.principal.PrincipalStrategy;
import com.swak.flux.security.principal.TokenPrincipalStrategy;
import com.swak.flux.transport.http.server.HttpServerProperties;
import com.swak.flux.transport.http.server.ReactiveServer;
import com.swak.security.JwtAuthProvider;

/**
 * 安全配置
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@ConditionalOnBean({ SecurityConfigurationSupport.class })
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableSecurity", matchIfMissing = true)
public class SecurityAutoConfiguration {

	@Autowired
	private SecurityConfigurationSupport securityConfig;

	public SecurityAutoConfiguration() {
		APP_LOGGER.debug("Loading Security Filter");
	}
	
	/**
	 * 默认的 身份管理策略 项目中可以覆盖， 统一使用默认的
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(PrincipalStrategy.class)
	public PrincipalStrategy principalStrategy(HttpServerProperties properties) {
		JwtAuthProvider jwt = new JwtAuthProvider(properties.getKeyStorePath(), properties.getKeyStorePass(), properties.getJwtTokenName());
		return new TokenPrincipalStrategy(jwt);
	}

	/**
	 * 安全管理器
	 * 
	 * @param realm
	 * @param principalStrategy
	 * @return
	 */
	@Bean
	public SecurityManager securityManager(PrincipalStrategy principalStrategy) {
		SecurityManager securityManager = new DefaultSecurityManager(securityConfig.getRealm(), principalStrategy);
		SecurityUtils.setSecurityManager(securityManager);
		return securityManager;
	}

	/**
	 * FilterChain 管理器
	 * 
	 * @return
	 */
	@Bean
	public FilterChainManager filterChainManager() {
		FilterChainManager chainManager = new DefaultFilterChainManager();

		// 可以配置新的filter
		Map<String, WebFilter> filters = securityConfig.getFilters();
		if (!CollectionUtils.isEmpty(filters)) {
			for (Map.Entry<String, WebFilter> entry : filters.entrySet()) {
				String name = entry.getKey();
				WebFilter filter = entry.getValue();
				chainManager.addFilter(name, filter);
			}
		}

		// 构建filter chain path - filters
		Map<String, String> chains = securityConfig.getChains();
		if (!CollectionUtils.isEmpty(chains)) {
			for (Map.Entry<String, String> entry : chains.entrySet()) {
				String url = entry.getKey();
				String chainDefinition = entry.getValue();
				chainManager.createChain(url, chainDefinition);
			}
		}
		return chainManager;
	}

	/**
	 * 设置安全filter
	 * 
	 * @return
	 */
	@Bean
	public SecurityFilter securityFilter(SecurityManager securityManager, FilterChainManager filterChainManager) {
		return new SecurityFilter(securityManager, filterChainManager);
	}
}
