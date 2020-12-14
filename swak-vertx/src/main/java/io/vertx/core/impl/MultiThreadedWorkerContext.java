package io.vertx.core.impl;

import io.vertx.core.spi.tracing.VertxTracer;

/**
 * 添加的类 - 可并发运行的 WorkerContext， 不需要顺序运行
 * 
 * @author lifeng
 * @date 2020年4月1日 下午9:26:40
 */
class MultiThreadedWorkerContext extends WorkerContext {

	final MultiTaskQueue tasQueue;

	MultiThreadedWorkerContext(VertxInternal vertx, VertxTracer<?, ?> tracer, WorkerPool internalBlockingPool,
			WorkerPool workerPool, Deployment deployment, CloseHooks closeHooks, ClassLoader tccl) {
		super(vertx, tracer, internalBlockingPool, workerPool, deployment, closeHooks, tccl);
		this.tasQueue = new MultiTaskQueue();
	}

	@Override
	protected TaskQueue taskQueue(AbstractContext ctx) {
		TaskQueue orderedTasks;
		if (ctx instanceof DuplicatedContext) {
			orderedTasks = ((DuplicatedContext) ctx).orderedTasks();
		} else {
			orderedTasks = this.tasQueue;
		}
		return orderedTasks;
	}
}