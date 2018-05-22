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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import com.swak.ApplicationProperties;
import com.swak.common.Constants;
import com.swak.common.cache.CacheProperties;
import com.swak.common.eventbus.system.SystemEventPublisher;
import com.swak.common.http.HttpClientProperties;
import com.swak.common.persistence.incrementer.IdGen;
import com.swak.common.serializer.FSTSerializer;
import com.swak.common.serializer.JavaSerializer;
import com.swak.common.serializer.KryoPoolSerializer;
import com.swak.common.serializer.KryoSerializer;
import com.swak.common.serializer.SerializationUtils;
import com.swak.common.serializer.Serializer;
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
@EnableConfigurationProperties(ApplicationProperties.class)
public class AppAutoConfiguration {

	/**
	 * 缓存 服务配置
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnMissingBean(CacheConfigurationSupport.class)
	@EnableConfigurationProperties(CacheProperties.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableRedis", matchIfMissing = true)
	public static class CacheAutoConfiguration extends CacheConfigurationSupport {
		public CacheAutoConfiguration() {
			APP_LOGGER.debug("Loading Redis Cache");
		}
	}
	
	/**
	 * Event bus
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnMissingBean(RedisEventBusConfigurationSupport.class)
	@AutoConfigureAfter(CacheAutoConfiguration.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableEventBus", matchIfMissing = true)
	public static class EventBusAutoConfiguration extends RedisEventBusConfigurationSupport {
		public EventBusAutoConfiguration() {
			APP_LOGGER.debug("Loading Event bus");
		}
	}
	
	/**
	 * 系统事件
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@AutoConfigureAfter(EventBusAutoConfiguration.class)
	@ConditionalOnMissingBean(SystemEventConfigurationSupport.class)
	public static class SystemEventAutoConfiguration extends SystemEventConfigurationSupport {
	}
	
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
		 * 默认的 身份管理策略
		 * 项目中可以覆盖
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
		public SecurityManager securityManager(PrincipalStrategy principalStrategy, SystemEventPublisher eventPublisher) {
			SecurityManager securityManager = new DefaultSecurityManager(securityConfig.getRealm(), principalStrategy, eventPublisher);
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
	 * 数据库配置
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableDataBase", matchIfMissing = true)
	public static class DataBaseAutoConfiguration extends DataBaseConfigurationSupport {
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
	@AutoConfigureAfter({SecurityConfiguration.class, DataBaseAutoConfiguration.class})
	public static class WebHandlerAutoConfiguration extends WebConfigurationSupport {
		public WebHandlerAutoConfiguration() {
			APP_LOGGER.debug("Loading Web Handler");
		}
	}

	/**
	 * session 支持
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 50)
	@Order(Ordered.HIGHEST_PRECEDENCE + 50)
	@ConditionalOnMissingBean(SessionConfigurationSupport.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableSession", matchIfMissing = true)
	@AutoConfigureAfter({SecurityConfiguration.class, CacheAutoConfiguration.class})
	public static class SessionAutoConfiguration extends SessionConfigurationSupport {
		public SessionAutoConfiguration() {
			APP_LOGGER.debug("Loading Session Manage");
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
	@AutoConfigureAfter({WebHandlerAutoConfiguration.class})
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
	 * HttpClient 服务配置
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 200)
	@Order(Ordered.HIGHEST_PRECEDENCE + 200)
	@ConditionalOnMissingBean(HttpClientConfigurationSupport.class)
	@EnableConfigurationProperties(HttpClientProperties.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableHttpClient", matchIfMissing = true)
	@Import(HttpClientConfigurationSupport.class)
	public static class HttpClientAutoConfiguration {
	}
	
	/**
	 * 基础组件
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 150)
	@Order(Ordered.HIGHEST_PRECEDENCE + 150)
	public static class BaseFuntionAutoConfiguration {
		
		/**
		 * 基础配置
		 * @param context
		 */
		public BaseFuntionAutoConfiguration(ApplicationContext context, ApplicationProperties properties) {
			APP_LOGGER.debug("Loading Base Function");
			
			// 简单的资源
			this.springContextHolder(context);
			this.serializer(properties);
			this.idGenerator(properties);
		}
		
		/**
		 * SpringContextHolder
		 * @param context
		 */
		public void springContextHolder(ApplicationContext context) {
			SpringContextHolder.setApplicationContext(context);
		}
		
		/**
		 * IdGenerator
		 */
		public void idGenerator(ApplicationProperties properties) {
			IdGen.setServerSn(properties.getServerSn());
		}
		
		/**
		 * 序列化
		 * @return
		 */
		public void serializer(ApplicationProperties properties) {
			String ser = properties.getSerialization();
			Serializer g_ser = null;
			if (ser.equals("java")) {
	            g_ser = new JavaSerializer();
	        } else if (ser.equals("fst")) {
	            g_ser = new FSTSerializer();
	        } else if (ser.equals("kryo")) {
	            g_ser = new KryoSerializer();
	        } else if (ser.equals("kryo_pool")){
	        	g_ser = new KryoPoolSerializer();
	        } else {
	        	g_ser = new JavaSerializer();
	        }
			
			// 公共引用
			SerializationUtils.g_ser = g_ser;
		}
		
		/**
		 * 管理一些生命周期
		 * @return
		 */
		@Bean
		public DisposeBean disposeBean() {
			return new DisposeBean();
		}
	}

	/**
	 * 系统服务
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 200)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableBooter", matchIfMissing = true)
	public static class AppListenerConfig {
		
		public AppListenerConfig() {
			APP_LOGGER.debug("Loading App Booter");
		}

		@Bean
		public AppBooter appBooter() {
			return new AppBooter();
		}
	}
}