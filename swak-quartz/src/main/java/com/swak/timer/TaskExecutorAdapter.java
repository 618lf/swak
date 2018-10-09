package com.swak.timer;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.utils.SpringContextHolder;

/**
 * 
 * 服务执行代理
 * 
 * @author lifeng
 */
public class TaskExecutorAdapter implements Job {

	public static Logger logger = LoggerFactory.getLogger(TaskExecutorAdapter.class);
	public static String JOB_TASK_KEY = "TASK_KEY";
    private static TaskServiceFacade taskService;
	
	static {
		taskService = SpringContextHolder.getBean(TaskServiceFacade.class);
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Long key = context.getMergedJobDataMap().getLong(JOB_TASK_KEY);
		Task task = taskService.preDoTask(key);
		TaskExecutor target = taskService.getExecutor(task);
		if (task != null && target != null) {
		    try {target.doTask(task);} catch (Exception e) {logger.error("任务执行失败:", e);}
		    taskService.postDoTask(task);
		}
	}
}
