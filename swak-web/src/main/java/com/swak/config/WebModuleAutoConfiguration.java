package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import com.swak.Constants;
import com.swak.eventbus.system.SystemEventPublisher;
import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.server.HttpServerProperties;
import com.swak.reactivex.server.ReactiveWebServerFactory;
import com.swak.security.SecurityFilter;
import com.swak.security.mgt.FilterChainManager;
import com.swak.security.mgt.SecurityManager;
import com.swak.security.mgt.support.DefaultFilterChainManager;
import com.swak.security.mgt.support.DefaultSecurityManager;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.principal.support.TokenPrincipalStrategy;
import com.swak.security.utils.SecurityUtils;

public class WebModuleAutoConfiguration {

	/**
	 * 安全配置
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnBean({ SecurityConfigurationSupport.class })
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableSecurity", matchIfMissing = true)
	public static class SecurityConfiguration {

		@Autowired
		private SecurityConfigurationSupport securityConfig;

		public SecurityConfiguration() {
			APP_LOGGER.debug("Loading Security Filter");
		}

		/**
		 * 默认的 身份管理策略 项目中可以覆盖
		 * 
		 * @return
		 */
		@Bean
		@ConditionalOnMissingBean(PrincipalStrategy.class)
		public PrincipalStrategy principalStrategy() {
			return new TokenPrincipalStrategy();
		}

		/**
		 * 安全管理器
		 * 
		 * @param realm
		 * @param principalStrategy
		 * @return
		 */
		@Bean
		public SecurityManager securityManager(PrincipalStrategy principalStrategy,
				SystemEventPublisher eventPublisher) {
			SecurityManager securityManager = new DefaultSecurityManager(securityConfig.getRealm(), principalStrategy,
					eventPublisher);
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
	
	/**
	 * Web 服务配置
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 20)
	@Order(Ordered.HIGHEST_PRECEDENCE + 20)
	@ConditionalOnMissingBean(WebConfigurationSupport.class)
	public static class WebHandlerAutoConfiguration extends WebConfigurationSupport {
		public WebHandlerAutoConfiguration() {
			APP_LOGGER.debug("Loading Web Handler");
		}
	}

	/**
	 * 服务器配置
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 100)
	@Order(Ordered.HIGHEST_PRECEDENCE + 100)
	@EnableConfigurationProperties(HttpServerProperties.class)
	@AutoConfigureAfter({ WebHandlerAutoConfiguration.class })
	public static class HttpServerAutoConfiguration {

		private HttpServerProperties properties;

		public HttpServerAutoConfiguration(HttpServerProperties properties) {
			this.properties = properties;
			APP_LOGGER.debug("Loading Http Server on http://" + properties.getHost() + ":" + properties.getPort());
		}

		@Bean
		public ReactiveWebServerFactory reactiveWebServerFactory() {
			return new ReactiveWebServerFactory(properties);
		}
	}
	
	/**
	 * session 支持
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 150)
	@Order(Ordered.HIGHEST_PRECEDENCE + 150)
	@ConditionalOnMissingBean(SessionConfigurationSupport.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableSession", matchIfMissing = true)
	public static class SessionAutoConfiguration extends SessionConfigurationSupport {}

}
