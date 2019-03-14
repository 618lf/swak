package com.swak.config.quartz;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * 属性配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(Constants.QUARTZ_PREFIX)
public class ScheduleProperties {

	private String instanceName = "SWAK-PLAN";
	private Integer threadCount = 10;
	private Boolean cleanShutdown = true;
	private Boolean threadsInheritContextClassLoaderOfInitializingThread = true;
	private Integer startupDelay = 10;
	
	public Integer getStartupDelay() {
		return startupDelay;
	}
	public void setStartupDelay(Integer startupDelay) {
		this.startupDelay = startupDelay;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public Integer getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(Integer threadCount) {
		this.threadCount = threadCount;
	}
	public Boolean getCleanShutdown() {
		return cleanShutdown;
	}
	public void setCleanShutdown(Boolean cleanShutdown) {
		this.cleanShutdown = cleanShutdown;
	}
	public Boolean getThreadsInheritContextClassLoaderOfInitializingThread() {
		return threadsInheritContextClassLoaderOfInitializingThread;
	}
	public void setThreadsInheritContextClassLoaderOfInitializingThread(
			Boolean threadsInheritContextClassLoaderOfInitializingThread) {
		this.threadsInheritContextClassLoaderOfInitializingThread = threadsInheritContextClassLoaderOfInitializingThread;
	}
}
