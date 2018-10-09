package com.swak.timer;

import java.util.List;

/**
 * 
 * 服务管理
 * 
 * @author lifeng
 */
public interface TaskServiceFacade {

	/**
	 * 得到服务
	 * 
	 * @param id
	 * @return
	 */
	Task get(Long id);

	/**
	 * 删除
	 * 
	 * @param task
	 */
	void delete(Task task);

	/**
	 * 修改操作
	 * 
	 * @param task
	 */
	void updateOps(Task task);

	/**
	 * 修改状态
	 * 
	 * @param task
	 */
	void updateStatus(Task task);
	
	/**
	 * 修改状态
	 * 
	 * @param task
	 */
	void updateStatus(List<Task> tasks);

	/**
	 * 可运行的任务
	 * 
	 * @return
	 */
	List<Task> runAbleTasks();

	/**
	 * 任务预先处理
	 * 
	 * @param id
	 * @return
	 */
	Task preDoTask(Long id);

	/**
	 * 任务后置处理
	 * 
	 * @param task
	 */
	void postDoTask(Task task);

	/**
	 * 执行的代码片段
	 * 
	 * @param task
	 * @return
	 */
	TaskExecutor getExecutor(Task task);
}
