package com.swak.schedule;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronSequenceGenerator;

import com.swak.utils.time.DateUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 任务执行器, 通过running 保证异步任务得执行完成之后才执行下一次任务。
 * 
 * @author lifeng
 */
@SuppressWarnings("deprecation")
public abstract class StandardExecutor implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(Task.class);
	private volatile AtomicBoolean running = new AtomicBoolean(false);
	private CronSequenceGenerator cronSequenceGenerator;
	private volatile Date nextDate;

	/**
	 * Spring CronExpression
	 * 
	 * @return
	 */
	protected abstract String cronExpression();

	/**
	 * 任务名称（唯一任务）
	 * 
	 * @return
	 */
	protected abstract String name();

	/**
	 * 任务描述
	 * 
	 * @return
	 */
	protected abstract String describe();

	/**
	 * 执行任务
	 */
	protected abstract Object doTask(TaskFrag frag);

	/**
	 * 初始化
	 */
	Runnable runnable() {
		cronSequenceGenerator = new CronSequenceGenerator(this.cronExpression());
		return this;
	}

	/**
	 * 执行任务
	 */
	@Override
	public void run() {

		// 尝试获取任务
		if (running.compareAndSet(false, true)) {

			// 发起调度（也可能同步執行）
			this.runTask().whenComplete((frag, ex) -> {

				// 之后后处理
				this.post(frag, ex);

				// 修改为可执行
				running.set(false);
			});

			// 调度线程结束
			return;
		}

		// 调试模式：检查调度任务的执行状态，如果长时间处理执行状态则可以遇到问题
		if (logger.isDebugEnabled()) {
			logger.debug("Warn: Task {} is running", this.name());
		}
	}

	/**
	 * 执行任务
	 */
	@SuppressWarnings("unchecked")
	private CompletableFuture<TaskFrag> runTask() {

		CompletableFuture<TaskFrag> future = new CompletableFuture<>();

		// 运行片段
		TaskFrag frag = new TaskFrag();

		// 是否需要执行
		if (!(nextDate == null || nextDate.before(frag.runTime))) {
			future.complete(frag.setSuccess(false));
			return future;
		}

		// 设置预计下次需要执行的时间（最後判斷是否需要調整）
		frag.setNextTime(cronSequenceGenerator.next(frag.getRunTime()));

		// 执行任务 - 可以返回异步结果
		Object result = null;
		Throwable ex = null;
		try {
			result = this.doTask(frag);
		} catch (Exception e) {
			ex = e;
		}

		// 执行任务
		if (result != null && result instanceof CompletionStage) {
			CompletionStage<Object> resultFuture = (CompletionStage<Object>) result;
			resultFuture.whenComplete((v, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(frag.setSuccess(true));
				}
			});
		} else if (ex != null) {
			future.completeExceptionally(ex);
		} else {
			future.complete(frag.setSuccess(true));
		}
		return future;
	}

	/**
	 * 执行任务之后
	 */
	private void post(TaskFrag frag, Throwable ex) {
		try {
			/**
			 * 异常处理
			 */
			if (ex != null) {
				logger.error("execute task[{}] error.", this.getClass().getName(), ex);
				return;
			}

			/**
			 * 调整下次执行时间
			 */
			this.adjustNextTime(frag);

			/**
			 * 调试模式
			 */
			if (logger.isDebugEnabled() && frag.isSuccess()) {
				logger.debug("Task {} Dispatch {}, Next Dispatch Time is {}", this.describe(),
						frag.isSuccess() ? "success" : "failure",
						nextDate != null ? DateUtils.getFormatDate(nextDate, "yyyy-MM-dd HH:mm:ss") : "调度中...");
			}
		} catch (Exception e) {
			logger.error("任务执行结束后：", e);
		}
	}

	/**
	 * 调整下次执行时间
	 * 
	 * @param frag
	 * @return
	 */
	private void adjustNextTime(TaskFrag frag) {

		// 执行成功才需要调整下次执行时间
		if (frag == null || !frag.isSuccess()) {
			return;
		}

		// 如果已经在当前时间之后
		nextDate = frag.getNextTime();
		Date now = DateUtils.getTimeStampNow();
		if (nextDate.before(now)) {
			nextDate = cronSequenceGenerator.next(now);
		}
	}

	/**
	 * 
	 * 任务片段
	 * 
	 * @author lifeng
	 */
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class TaskFrag {

		/**
		 * 调度时间
		 */
		Date runTime = DateUtils.getTimeStampNow();

		/**
		 * 调度结果
		 */
		boolean success = Boolean.TRUE;

		/**
		 * 预计下次执行时间(需要调度才设置，不需要则不设置)
		 */
		Date nextTime = null;
	}
}