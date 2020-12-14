package io.vertx.core.impl;

import java.util.concurrent.Executor;

/**
 * 添加的类 -  直接将代码提交到线程池中
 * 
 * @author lifeng
 * @date 2020年4月1日 下午9:40:46
 */
public class MultiTaskQueue extends TaskQueue {

	/**
	 * 不做顺序执行
	 */
	@Override
	public void execute(Runnable task, Executor executor) {
		executor.execute(task);
	}
}