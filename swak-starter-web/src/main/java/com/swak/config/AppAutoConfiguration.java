package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.ApplicationProperties;
import com.swak.Constants;
import com.swak.incrementer.IdGen;
import com.swak.reactivex.booter.AppBooter;
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
			APP_LOGGER.debug("Loading Base Function");

			// 简单的资源
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

	/**
	 * Worker Executor 配置
	 * 
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 150)
	@Order(Ordered.HIGHEST_PRECEDENCE + 150)
	@ConditionalOnMissingBean(ExecutorConfigurationSupport.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableWorkers", matchIfMissing = true)
	@Import(ExecutorConfigurationSupport.class)
	public static class ExecutorAutoConfiguration {}

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