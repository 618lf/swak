package com.swak.schedule;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 有序的定时任务
 * 
 * @author lifeng
 */
public class CronTrigger {

	private final TaskExecutor task;
	private final ScheduledExecutorService scheduler;
	private volatile ScheduledFuture<?> future;

	public CronTrigger(ScheduledExecutorService scheduler, TaskExecutor task) {
		this.scheduler = scheduler;
		this.task = task;
	}

	/**
	 * 执行定时任务
	 */
	public synchronized void schedule() {
		if (future == null || future.isDone()) {
			future = this.scheduler.schedule(task, 0, TimeUnit.SECONDS);
		}
	}
}