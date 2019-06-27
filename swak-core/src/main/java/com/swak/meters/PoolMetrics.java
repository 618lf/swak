package com.swak.meters;

/**
 * An SPI used internally by Vert.x to gather metrics on pools used by Vert.x  (execute blocking, worker verticle or data source).
 * <p>
 * Usually these metrics measure the latency of a task queuing and the latency a task execution.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public interface PoolMetrics {

	/**
	 * A new task has been submitted to access the resource. This method is called
	 * from the submitter context.
	 *
	 * @return the timer measuring the task queuing
	 */
	default Object submitted() {
		return null;
	}

	/**
	 * The submitted task start to use the resource.
	 *
	 * @param t
	 *            the timer measuring the task queuing returned by
	 *            {@link #submitted()}
	 * @return the timer measuring the task execution
	 */
	default void begin(Object t) {
	}

	/**
	 * The task has been rejected. The underlying resource has probably be shutdown.
	 *
	 * @param t
	 *            the timer measuring the task queuing returned by
	 *            {@link #submitted()}
	 */
	default void rejected(Object t) {
	}

	/**
	 * The submitted tasks has completed its execution and release the resource.
	 *
	 * @param succeeded
	 *            whether or not the task has gracefully completed
	 * @param t
	 *            the timer measuring the task execution returned by {@link #begin}
	 */
	default void end(Object t, boolean succeeded) {
	}
}
