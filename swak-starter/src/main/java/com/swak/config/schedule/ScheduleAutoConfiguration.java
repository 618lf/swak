package com.swak.config.schedule;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.schedule.StandardExecutor;
import com.swak.schedule.TaskScheduled;

/**
 * 简单的定时任务
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(TaskScheduled.class)
@EnableConfigurationProperties(ScheduleProperties.class)
public class ScheduleAutoConfiguration {

	@Autowired
	private ScheduleProperties properties;

	/**
	 * 统一的定时任务处理
	 * 
	 * @return
	 */
	@Bean
	public TaskScheduled secondLevelScheduled(ObjectProvider<List<StandardExecutor>> tasks) {
		// 标准的任务管理器
		TaskScheduled taskScheduled = new TaskScheduled(properties.getCoreThreads(), properties.getPeriodSeconds());

		// 默认启动直接配置的任务
		return taskScheduled.scheduleTasks(tasks.getIfAvailable());
	}
}
