package com.swak.schedule;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.closable.ShutDownHook;
import com.swak.reactivex.threads.Contexts;

/**
 * 10 秒执行一次的任务，不要有长耗时的任务
 * 
 * @author lifeng
 */
public class TaskScheduled implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(Task.class);
	private ScheduledExecutorService scheduler;
	private List<CronTrigger> triggers;
	private final int coreThreads;
	private final int periodSeconds;

	/**
	 * 定义定时器
	 * 
	 * @param coreThreads
	 * @param periodSeconds
	 */
	public TaskScheduled(int coreThreads, int periodSeconds) {

		// 标注参数
		this.coreThreads = coreThreads;
		this.periodSeconds = periodSeconds;

		// 启动任务
		this.init();
	}

	/**
	 * 之后加入校验程序，校验任务的执行时长
	 * 
	 * @param tasks
	 */
	private void init() {
		// 任务执行器
		scheduler = Contexts.createScheduledContext("Task.", coreThreads, true, 60, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(this, 10, periodSeconds, TimeUnit.SECONDS);
		ShutDownHook.registerShutdownHook(() -> {
			scheduler.shutdownNow();
		});
		// 任务容器
		triggers = new CopyOnWriteArrayList<CronTrigger>();
	}

	/**
	 * 启动计划任务
	 * 
	 * @param tasks
	 * @return
	 */
	public TaskScheduled scheduleTasks(List<StandardExecutor> tasks) {
		if (tasks != null && tasks.size() > 0) {
			for (StandardExecutor task : tasks) {
				CronTrigger trigger = new CronTrigger(scheduler, task);
				triggers.add(trigger);
			}
		}
		return this;
	}

	/**
	 * 启动计划任务
	 * 
	 * @param tasks
	 * @return
	 */
	public TaskScheduled scheduleTasks(StandardExecutor... tasks) {
		if (tasks != null && tasks.length > 0) {
			for (StandardExecutor task : tasks) {
				CronTrigger trigger = new CronTrigger(scheduler, task);
				triggers.add(trigger);
			}
		}
		return this;
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