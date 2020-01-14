package com.swak.dispatcher;

import java.util.concurrent.CompletionStage;

import com.swak.rabbit.EventBus;
import com.swak.schedule.ClusterTaskDispatcher;
import com.swak.schedule.Task;
import com.swak.schedule.TaskEvent;

public class RabbitMqDispatcher extends ClusterTaskDispatcher {

	public RabbitMqDispatcher(Task task) {
		super(task);
	}

	@Override
	protected CompletionStage<Void> doDispatch(TaskEvent event) {
		return EventBus.me().submit(name(), name(), event);
	}
}
