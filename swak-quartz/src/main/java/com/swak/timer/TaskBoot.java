package com.swak.timer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.boot.AbstractBoot;
import com.swak.timer.Task.TaskStatus;

/**
 * 定时任务加载器
 * 
 * @author lifeng
 */
public class TaskBoot extends AbstractBoot {

	@Autowired
	private TaskServiceFacade taskService;
	
	@Autowired
	private TaskCommandService taskCommandService;

	@Override
	public String describe() {
		return "定时任务加载项";
	}

	@Override
	public void init() {
		// 加载定时任务
		List<Task> tasks = taskService.runAbleTasks();
		for (Task task : tasks) {
			task.setTaskStatus(TaskStatus.RUNABLE);
		}
		taskService.updateStatus(tasks);
		
		// 注册任务
		for (Task task : tasks) {
			taskCommandService.start(task.getId());
		}
	}
}