package com.swak.config.quartz;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定时任务配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ Scheduler.class})
public class QuartzAutoConfiguration {

	private final QuartzProperties properties;

	private final List<SchedulerFactoryBeanCustomizer> customizers;

	private final JobDetail[] jobDetails;

	private final Map<String, Calendar> calendars;

	private final Trigger[] triggers;

	private final ApplicationContext applicationContext;

	public QuartzAutoConfiguration(QuartzProperties properties,
			ObjectProvider<List<SchedulerFactoryBeanCustomizer>> customizers,
			ObjectProvider<JobDetail[]> jobDetails,
			ObjectProvider<Map<String, Calendar>> calendars,
			ObjectProvider<Trigger[]> triggers, ApplicationContext applicationContext) {
		this.properties = properties;
		this.customizers = customizers.getIfAvailable();
		this.jobDetails = jobDetails.getIfAvailable();
		this.calendars = calendars.getIfAvailable();
		this.triggers = triggers.getIfAvailable();
		this.applicationContext = applicationContext;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SchedulerFactoryBean quartzScheduler() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setJobFactory(new AutowireCapableBeanJobFactory(
				this.applicationContext.getAutowireCapableBeanFactory()));
		if (!this.properties.getProperties().isEmpty()) {
			schedulerFactoryBean
					.setQuartzProperties(asProperties(this.properties.getProperties()));
		}
		if (this.jobDetails != null && this.jobDetails.length > 0) {
			schedulerFactoryBean.setJobDetails(this.jobDetails);
		}
		if (this.calendars != null && !this.calendars.isEmpty()) {
			schedulerFactoryBean.setCalendars(this.calendars);
		}
		if (this.triggers != null && this.triggers.length > 0) {
			schedulerFactoryBean.setTriggers(this.triggers);
		}
		customize(schedulerFactoryBean);
		return schedulerFactoryBean;
	}
	
	private Properties asProperties(Map<String, String> source) {
		Properties properties = new Properties();
		properties.putAll(source);
		return properties;
	}

	private void customize(SchedulerFactoryBean schedulerFactoryBean) {
		if (this.customizers != null) {
			for (SchedulerFactoryBeanCustomizer customizer : this.customizers) {
				customizer.customize(schedulerFactoryBean);
			}
		}
	}
}