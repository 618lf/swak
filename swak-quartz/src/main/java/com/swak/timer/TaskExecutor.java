package com.swak.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务执行器
 * 
 * @author lifeng
 */
public interface TaskExecutor {

	/**
	 * 日志记录
	 */
	public static Logger logger = LoggerFactory.getLogger(TaskExecutor.class);

	/**
	 * 显示名称
	 * 
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * 执行类调用的方法
	 * 
	 * @return
	 */
	public abstract Boolean doTask(Task task);
}