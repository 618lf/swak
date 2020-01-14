package com.swak.schedule;

/**
 * 基于 Task 的任务
 * 
 * @author lifeng
 */
public abstract class ClusterTaskDispatcher extends ClusterDispatcher {

	private final Task task;

	public ClusterTaskDispatcher(Task task) {
		this.task = task;
	}

	@Override
	protected String cronExpression() {
		return task.getCronExpression();
	}

	@Override
	protected String name() {
		return task.getId();
	}

	@Override
	protected String describe() {
		return task.getName();
	}
}
