package com.swak.config.schedule;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.schedule.StandardExecutor;
import com.swak.schedule.TaskScheduler;

/**
 * 简单的定时任务
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(TaskScheduler.class)
public class ScheduleAutoConfiguration {

	@Autowired
	private ScheduleProperties properties;
	
	/**
	 * 统一的定时任务处理
	 * 
	 * @return
	 */
	@Bean
	public TaskScheduler secondLevelScheduled(ObjectProvider<List<StandardExecutor>> tasks) {
		return new TaskScheduler(properties.getCoreThreads(), tasks.getIfAvailable());
	}
}
