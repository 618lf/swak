package com.swak.schedule;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.closable.ShutDownHook;
import com.swak.utils.Lists;

/**
 * 10 秒执行一次的任务，不要有长耗时的任务
 * 
 * @author lifeng
 */
public class TaskScheduled implements Runnable {

	private ScheduledExecutorService scheduler;
	private List<CronTrigger> triggers;

	/**
	 * 之后加入校验程序，校验任务的执行时长
	 * 
	 * @param tasks
	 */
	public TaskScheduled(List<TaskExecutor> tasks) {
		scheduler = Executors.newScheduledThreadPool(2, new ThreadFactory() {
			private AtomicInteger count = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				t.setName("Task-scheduler-" + count.getAndIncrement());
				return t;
			}
		});
		scheduler.scheduleAtFixedRate(this, 10, 10, TimeUnit.SECONDS);
		ShutDownHook.registerShutdownHook(() -> {
			scheduler.shutdownNow();
		});
		this.initTriggers(tasks);
	}

	// 初始化促发器
	private void initTriggers(List<TaskExecutor> tasks) {
		triggers = Lists.newArrayList();
		for (TaskExecutor task : tasks) {
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