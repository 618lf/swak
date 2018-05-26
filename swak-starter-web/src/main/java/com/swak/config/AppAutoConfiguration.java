package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.ApplicationProperties;
import com.swak.Constants;
import com.swak.eventbus.system.SystemEventPublisher;
import com.swak.executor.Workers;
import com.swak.incrementer.IdGen;
import com.swak.reactivex.booter.AppBooter;
import com.swak.reactivex.server.HttpServerProperties;
import com.swak.serializer.FSTSerializer;
import com.swak.serializer.JavaSerializer;
import com.swak.serializer.KryoPoolSerializer;
import com.swak.serializer.KryoSerializer;
import com.swak.serializer.SerializationUtils;
import com.swak.serializer.Serializer;
import com.swak.utils.SpringContextHolder;

/**
 * 系统配置
 * 
 * @author lifeng
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class AppAutoConfiguration {
	
	//----------------- 缓存 --------------------
	/**
	 * 会判断是否引入了缓存组件
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnClass(name="com.swak.config.CacheModuleAutoConfiguration")
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableRedis", matchIfMissing = true)
	@Import({CacheModuleAutoConfiguration.class})
	public static class CacheConfiguration {}
	
	
	//----------------- 数据库 --------------------
	/**
	 * 会判断是否引入了数据库组件
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnClass(name="com.swak.config.DataBaseAutoConfiguration")
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableDataBase", matchIfMissing = true)
	@Import({DataBaseAutoConfiguration.class})
	public static class JdbcConfiguration {}
	
	
	//----------------- WEB SERVER --------------------
	/**
	 * web 服务配置
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnClass(name="com.swak.config.WebModuleAutoConfiguration")
	@Import({WebModuleAutoConfiguration.class})
	public static class WebAutoConfiguration {}
	
	//----------------- 系统事件 --------------------
	/**
	 * 系统事件
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnMissingBean(SystemEventPublisher.class)
	public static class SystemEventAutoConfiguration {
		
		/**
		 * 如果没有这个启动一个空的实现
		 * @param eventProducer
		 * @return
		 */
		@Bean
		public SystemEventPublisher noSystemEventPublisher() {
			return new SystemEventPublisher() {
				@Override
				public void publishError(Throwable t) {}

				@Override
				public void publishSignIn(Object subject) {}

				@Override
				public void publishSignUp(Object subject) {}

				@Override
				public void publishLogout(Object subject) {}
			};
		}
	}
	
	//----------------- 监控 --------------------
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 50)
	@Order(Ordered.HIGHEST_PRECEDENCE + 50)
	@ConditionalOnClass(name = {"com.swak.actuator.endpoint.annotation.Endpoint"})
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableActuator", matchIfMissing = true)
	public static class ActuatorAutoConfiguration {
		
		public ActuatorAutoConfiguration() {
			APP_LOGGER.debug("Loading Endpoint Actuator");
		}
		
		@ComponentScan({"com.swak.actuator.config"})
		public static class ActuatorConfiguration {}
	}
	
	//----------------- 基础组件 --------------------
	
	/**
	 * 基础组件
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 150)
	@Order(Ordered.HIGHEST_PRECEDENCE + 150)
	public static class BaseFuntionAutoConfiguration {

		/**
		 * 基础配置
		 * 
		 * @param context
		 */
		public BaseFuntionAutoConfiguration(ApplicationContext context, ApplicationProperties properties) {
			this.springContextHolder(context);
			this.serializer(properties);
			this.idGenerator(properties);
		}

		/**
		 * SpringContextHolder
		 * 
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
		 * 
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
			} else if (ser.equals("kryo_pool")) {
				g_ser = new KryoPoolSerializer();
			} else {
				g_ser = new JavaSerializer();
			}

			// 公共引用
			SerializationUtils.g_ser = g_ser;
		}
	}

	//----------------- WORKER EXECUTOR --------------------
	
	/**
	 * Worker Executor 配置
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 150)
	@Order(Ordered.HIGHEST_PRECEDENCE + 150)
	@ConditionalOnMissingBean(Executor.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableWorkers", matchIfMissing = true)
	public static class ExecutorAutoConfiguration {
		
		@Autowired
		private HttpServerProperties properties;
		
		public ExecutorAutoConfiguration() {
			APP_LOGGER.debug("Loading Worker Executor");
		}
		
		@Bean
		public Executor workerExecutor() {
			Executor executor = null;
			if (properties.getWorkerThreads() == -1) {
				executor = ForkJoinPool.commonPool();
			} else {
				executor = Executors.newFixedThreadPool(properties.getWorkerThreads(), threadFactory("SWAK-worker"));
			}
			Workers.executor(executor);
			return Workers.executor();
		}
		
		/**
		 * 线程管理器
		 * @param parent
		 * @param prefix
		 * @return
		 */
		ThreadFactory threadFactory(String prefix) {
			AtomicInteger counter = new AtomicInteger();
			return new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					t.setDaemon(true);
					t.setName(prefix + "-" + counter.incrementAndGet());
					return t;
				}
			};
		}
	}
	
	//----------------- HTTP CLIENT --------------------
	
	/**
	 * 会判断是否引入了HTTP组件
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 150)
	@Order(Ordered.HIGHEST_PRECEDENCE + 150)
	@ConditionalOnClass(name="com.swak.config.HttpClientAutoConfiguration")
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableHttpClient", matchIfMissing = true)
	@Import({HttpClientAutoConfiguration.class})
	public static class HttpConfiguration {}

	//----------------- APP LISTENER --------------------
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