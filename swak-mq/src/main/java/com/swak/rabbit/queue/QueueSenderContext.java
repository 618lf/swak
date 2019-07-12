package com.swak.rabbit.queue;

import java.util.concurrent.ExecutorService;

/**
 * @author lifeng
 */
public class QueueSenderContext {

	private ExecutorService executor = null;

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	/**
	 * 执行
	 * 
	 * @param command
	 */
	public void execute(Runnable command) {
		executor.execute(command);
	}
}