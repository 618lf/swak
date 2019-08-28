package com.swak.schedule;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronSequenceGenerator;

import com.swak.utils.time.DateUtils;

/**
 * 任务执行器, 通过running 保证异步任务得执行完成之后才执行下一次任务。
 * 
 * @author lifeng
 */
public abstract class StandardExecutor implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(StandardExecutor.class);
	private volatile AtomicBoolean running = new AtomicBoolean(false);
	private CronSequenceGenerator cronSequenceGenerator;
	private Date nextDate;

	public StandardExecutor() {
		cronSequenceGenerator = new CronSequenceGenerator(this.cronExpression());
	}

	/**
	 * Spring CronExpression
	 * 
	 * @return
	 */
	protected abstract String cronExpression();
	
	/**
	 * 执行任务
	 */
	protected abstract Object doTask();

	/**
	 * 执行任务
	 */
	@Override
	public void run() {
		if (running.compareAndSet(false, true)) {
			this.runTask().whenComplete((v, e) -> {
				this.postTask(e);
			});
		}
	}

	/**
	 * 执行任务
	 */
	@SuppressWarnings("unchecked")
	private CompletableFuture<Boolean> runTask() {

		CompletableFuture<Boolean> future = new CompletableFuture<>();

		// 是否需要执行
		if (!(nextDate == null || nextDate.before(DateUtils.getTimeStampNow()))) {
			future.complete(false);
			return future;
		}

		// 执行任务 - 可以返回异步结果
		Object result = null;
		Throwable ex = null;
		try {
			result = this.doTask();
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
					future.complete(true);
				}
			});
		} else if (ex != null) {
			future.completeExceptionally(ex);
		} else {
			future.complete(true);
		}
		return future;
	}

	/**
	 * 执行任务之后
	 */
	protected void postTask(Throwable ex) {
		if (ex != null) {
			logger.error("execute task[{}] error.", this.getClass().getName(), ex);
		}
		if (running.compareAndSet(true, false)) {
			nextDate = cronSequenceGenerator.next(DateUtils.getTimeStampNow());
		}
	}
}