package com.swak.config.quartz;

import java.util.Properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.quartz.SchedulerFactoryBean;

/**
 * 
 * 计划自动配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(SchedulerFactoryBean.class)
@EnableConfigurationProperties(ScheduleProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableSchedule", matchIfMissing = true)
public class ScheduleAutoConfiguration {

	/**
	 * 本地配置
	 * 
	 * @return
	 */
	@Bean
	public SchedulerFactoryBean localQuartzScheduler(ScheduleProperties properties) {
		SchedulerFactoryBean localQuartzScheduler = new SchedulerFactoryBean();
		Properties quartzProperties = new Properties();
		quartzProperties.setProperty("org.quartz.scheduler.instanceName", properties.getInstanceName());
		quartzProperties.setProperty("org.quartz.threadPool.threadCount", String.valueOf(properties.getThreadCount()));
		quartzProperties.setProperty("org.quartz.plugin.shutdownhook.class",
				"org.quartz.plugins.management.ShutdownHookPlugin");
		quartzProperties.setProperty("org.quartz.plugin.shutdownhook.cleanShutdown",
				String.valueOf(properties.getCleanShutdown()));
		quartzProperties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread",
				String.valueOf(properties.getThreadsInheritContextClassLoaderOfInitializingThread()));
		localQuartzScheduler.setQuartzProperties(quartzProperties);
		localQuartzScheduler.setStartupDelay(properties.getStartupDelay());
		return localQuartzScheduler;
	}
}
