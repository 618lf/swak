package com.swak.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import com.swak.common.utils.SpringContextHolder;
import com.swak.reactivex.booter.AppBooter;
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

/**
 * 系统配置
 * 
 * @author lifeng
 */
@Configuration
public class AppAutoConfiguration {

	/**
	 * 基础服务
	 * 
	 * @return
	 */
	@Order(1)
	@Bean
	public SpringContextHolder springContextHolder() {
		return new SpringContextHolder();
	}

	/**
	 * 安全配置
	 * 
	 * @author lifeng
	 */
	@Order(2)
	@Configuration
	@ConditionalOnProperty(prefix = "spring.security", name = "enabled", matchIfMissing = true)
	@ConditionalOnBean({ SecurityConfigurationSupport.class })
	public static class SecurityConfiguration {

		@Autowired
		private SecurityConfigurationSupport securityConfig;
		
		/**
		 * 身份管理策略
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

	/**
	 * Web 服务配置
	 * 
	 * @author lifeng
	 */
	@Order(3)
	@Configuration
	@ConditionalOnMissingBean(WebConfigurationSupport.class)
	public static class WebAutoConfiguration extends WebConfigurationSupport {
	}

	/**
	 * 服务器配置
	 * 
	 * @author lifeng
	 */
	@Order(4)
	@Configuration
	@EnableConfigurationProperties(HttpServerProperties.class)
	public static class WebServerAutoConfiguration {

		private HttpServerProperties properties;

		public WebServerAutoConfiguration(HttpServerProperties properties) {
			this.properties = properties;
		}

		@Bean
		public ReactiveWebServerFactory reactiveWebServerFactory() {
			return new ReactiveWebServerFactory(properties);
		}
	}

	/**
	 * 系统服务
	 * 
	 * @author lifeng
	 */
	@Order(5)
	@Configuration
	public static class AppListenerConfig {

		@Bean
		public AppBooter appBooter() {
			return new AppBooter();
		}
	}
}