package com.swak.schedule;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 默认实现
 * 
 * @author lifeng
 */
public abstract class AbstractTaskConsumer {

	/**
	 * 消费任务
	 * 
	 * @param message
	 * @return
	 */
	public CompletionStage<Boolean> tryConsume(TaskEvent event) {
		Task task = new Task();
		task.setTask(event.getTask());
		task.setCurrDispatchNo(event.getDispatch());
		task.setNextExecutionTime(event.getNextTime() != null ? new Date(event.getNextTime()) : null);
		return getTaskService().prepareExecution(task).thenCompose(res -> {
			if (res != null) {
				return this.doTask(res);
			}
			return CompletableFuture.completedFuture(null);
		}).thenCompose(res -> {
			return this.postConsumed(res);
		});
	}

	/**
	 * 执行消费处理
	 * 
	 * @param task
	 * @return
	 */
	public abstract CompletionStage<Task> doTask(Task task);

	/**
	 * 消费的后处理
	 * 
	 * @return
	 */
	public CompletionStage<Boolean> postConsumed(Task task) {
		if (task != null) {
			return getTaskService().postExecution(task).thenApply(res -> Boolean.TRUE);
		}
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}

	/**
	 * 在父类中无法被解析到，后续处理
	 * 
	 * @return
	 */
	public abstract TaskService getTaskService();
}
