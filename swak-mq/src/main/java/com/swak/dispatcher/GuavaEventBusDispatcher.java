package com.swak.dispatcher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.eventbus.EventBus;
import com.swak.schedule.ClusterTaskDispatcher;
import com.swak.schedule.Task;
import com.swak.schedule.TaskEvent;

/**
 * 基于 Guava 的任务调度
 * 
 * @author lifeng
 */
public class GuavaEventBusDispatcher extends ClusterTaskDispatcher {

	public GuavaEventBusDispatcher(Task task) {
		super(task);
	}

	@Override
	protected CompletionStage<Void> doDispatch(TaskEvent event) {
		EventBus.me().post(event);
		return CompletableFuture.completedFuture(null);
	}
}
