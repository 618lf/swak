package io.vertx.core.impl;

import io.vertx.core.spi.tracing.VertxTracer;

/**
 * 可并发运行的 WorkerContext
 * 
 * @author lifeng
 * @date 2020年4月1日 下午9:26:40
 */
class MultiThreadedWorkerContext extends WorkerContext {

	MultiThreadedWorkerContext(VertxInternal vertx, VertxTracer<?, ?> tracer, WorkerPool internalBlockingPool,
			WorkerPool workerPool, Deployment deployment, ClassLoader tccl) {
		super(vertx, tracer, internalBlockingPool, workerPool, deployment, tccl);
		this.orderedTasks = new MultiTaskQueue();
	}
}