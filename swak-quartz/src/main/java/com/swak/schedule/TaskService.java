package com.swak.schedule;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 任务服务
 * 
 * @author lifeng
 */
public interface TaskService {

	/**
	 * 获得所有需要启动的定时任务
	 * 
	 * @return
	 */
	CompletableFuture<List<Task>> queryActiveTasks();

	/**
	 * 准备发送消息
	 * 
	 * @param task
	 * @return
	 */
	CompletableFuture<Task> prepareExecution(Task task);

	/**
	 * 消息发送之后處理
	 * 
	 * @param task
	 */
	CompletableFuture<Void> postExecution(Task task);
}
