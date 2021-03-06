package com.swak.schedule;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.incrementer.UUIdGenerator;

/**
 * 集群版本的处理器
 * 
 * @author lifeng
 */
public abstract class ClusterDispatcher extends StandardExecutor {

	/**
	 * 调度执行任务
	 * 
	 */
	@Override
	protected Object doTask(TaskFrag frag) {
		return this.tryDispatch().thenCompose(res -> {
			if (res) {
				return this.doDispatch(new TaskEvent().setTask(name()).setDispatch(UUIdGenerator.uuid())
						.setNextTime(frag.getNextTime() != null ? frag.getNextTime().getTime() : null));
			}
			return CompletableFuture.completedFuture(null);
		});
	}

	/**
	 * 判断是否需要调度
	 * 
	 * @return
	 */
	protected CompletionStage<Boolean> tryDispatch() {
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}

	/**
	 * 执行调度
	 * 
	 * @return
	 */
	protected abstract CompletionStage<Void> doDispatch(TaskEvent event);
}