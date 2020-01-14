package com.swak.schedule;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.closable.ShutDownHook;
import com.swak.reactivex.threads.Contexts;
import com.swak.utils.Lists;

/**
 * 10 秒执行一次的任务，不要有长耗时的任务
 * 
 * @author lifeng
 */
public class TaskScheduled implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(Task.class);
	private ScheduledExecutorService scheduler;
	private List<CronTrigger> triggers;

	/**
	 * 之后加入校验程序，校验任务的执行时长
	 * 
	 * @param tasks
	 */
	public void init(List<StandardExecutor> tasks) {
		scheduler = Contexts.createScheduledContext("Task.", 1, true, 60, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(this, 10, 10, TimeUnit.SECONDS);
		ShutDownHook.registerShutdownHook(() -> {
			scheduler.shutdownNow();
		});
		this.initTriggers(tasks);
	}

	// 初始化促发器
	private void initTriggers(List<StandardExecutor> tasks) {
		triggers = Lists.newArrayList();
		for (StandardExecutor task : tasks) {
			CronTrigger trigger = new CronTrigger(scheduler, task);
			triggers.add(trigger);
		}
	}

	/**
	 * 周期的添加任务到定时任务中，也可以自己执行完成后自己添加也可以
	 */
	@Override
	public void run() {
		if (logger.isDebugEnabled()) {
			logger.debug("Schedule Tasks {}", triggers.toString());
		}
		triggers.stream().forEach((trigger) -> {
			trigger.schedule();
		});
	}
}