package com.swak.schedule;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.swak.closable.ShutDownHook;
import com.swak.reactivex.threads.Contexts;
import com.swak.utils.Lists;

/**
 * 10 秒执行一次的任务，不要有长耗时的任务
 * 
 * @author lifeng
 */
public class TaskScheduler implements Runnable {

	private ScheduledExecutorService scheduler;
	private List<CronTrigger> triggers;

	/**
	 * 之后加入校验程序，校验任务的执行时长
	 * 
	 * @param tasks
	 */
	public TaskScheduler(Integer coreThreads, List<StandardExecutor> tasks) {
		scheduler = Contexts.createScheduledContext("Task.", coreThreads, true, 60, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(this, 60, 10, TimeUnit.SECONDS);
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
		triggers.stream().forEach((trigger) -> {
			trigger.schedule();
		});
	}
}