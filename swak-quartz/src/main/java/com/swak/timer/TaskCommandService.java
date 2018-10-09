package com.swak.timer;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.quartz.CronExpression;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.swak.timer.Task.TaskStatus;
import com.swak.utils.SpringContextHolder;
import com.swak.utils.StringUtils;

/**
 * 
 * 任务执行命令
 * 
 * @author lifeng
 */
public class TaskCommandService {

	//任务
	private String TASK_PREFIX = "TASK-";
	
	private Scheduler scheduler;
	private TaskServiceFacade taskService;
	
	/**
	 * 删除任务
	 */
	public void remove(Long id) {
		Task task = this.taskService.get(id);
		task.setOps("任务删除失败");
		if (this.jobShutdown(task)) {
			taskService.delete(task);
		} else {
			taskService.updateOps(task);
		}
	}
	
	/**
	 * 启动任务
	 */
	public void start(Long id) {
		Task task = this.taskService.get(id);
		task.setTaskStatus(TaskStatus.FINISH);
		task.setOps("任务启动失败");
        if (isValidExpression(task)) {
        	if (this.jobRunable(task)) {
    			task.setTaskStatus(TaskStatus.RUNABLE);
    			task.setOps("任务启动成功");
    		}
		}
        taskService.updateStatus(task);
	}

	/**
	 * 停止任务
	 */
	public void stop(Long id) {
		Task task = this.taskService.get(id);
		task.setOps("任务停止失败");
		if (this.jobShutdown(task)) {
			task.setTaskStatus(TaskStatus.FINISH);
			task.setOps("任务停止成功");
		}
		taskService.updateStatus(task);
	}

	/**
	 * 暂停
	 */
	public void pause(Long id) {
		Task task = this.taskService.get(id);
		task.setOps("任务暂停失败");
		if (this.jobPause(task)) {
			task.setTaskStatus(TaskStatus.WAIT);
			task.setOps("任务暂停成功");
		}
		taskService.updateStatus(task);
	}

	/**
	 * 执行中
	 */
	public void execute(Long id) {
		Task task = this.taskService.get(id);
		task.setOps("任务执行失败");
		if (task.getTaskStatus() != TaskStatus.RUNNING) {
			this.jobExecute(task);
			task.setOps("任务执行成功");
		}
		taskService.updateStatus(task);
	}
	
	/**
	 * 任务可执行(等待执行)
	 * @param task
	 */
	public boolean jobRunable(Task task) {
		 try {
			CronTriggerImpl trigger = (CronTriggerImpl) this.scheduler.getTrigger(TriggerKey.triggerKey(TASK_PREFIX + task.getId()));
			if (null == trigger) {
				JobDetail jobDetail = this.getJobDetail(task, false);
				if(jobDetail != null) {
				   trigger = this.getCronTrigger(jobDetail, task);
				   scheduler.scheduleJob(jobDetail, trigger);
				   return true;
				}
			} else {
				trigger.setCronExpression(task.getCronExpression());
				scheduler.rescheduleJob(trigger.getKey(), trigger);
				return true;
			}
		} catch (Exception e) {return false;}
		return false;
	}
	
	/**
	 * 任务不可执行（暂停）
	 * @param task
	 */
	public boolean jobPause(Task task) {
		try {
			CronTriggerImpl trigger = (CronTriggerImpl) this.scheduler.getTrigger(TriggerKey.triggerKey(TASK_PREFIX + task.getId()));
			if(null == trigger) {
				return true;
			} else {
				scheduler.pauseTrigger(trigger.getKey());
				return true;
			}
		} catch (Exception e) {return false;}
	}
	
	/**
	 * 删除
	 * @return
	 */
	public boolean jobShutdown(Task task) {
		try {
			CronTriggerImpl trigger = (CronTriggerImpl) this.scheduler.getTrigger(TriggerKey.triggerKey(TASK_PREFIX + task.getId()));
			if(null == trigger) {
				return true;
			} else {
				scheduler.deleteJob(trigger.getJobKey());
				return true;
			}
		} catch (Exception e) {return false;}
	}
	
	/**
	 * 立即执行
	 * 1. 如果之没任务，则创建之后执行一次，然后删除 
	 * @param task
	 * @return
	 */
	public boolean jobExecute(Task task) {
		try {
			CronTriggerImpl trigger = (CronTriggerImpl) this.scheduler.getTrigger(TriggerKey.triggerKey(TASK_PREFIX + task.getId()));
			if (null == trigger) {
				JobDetail jobDetail = this.getJobDetail(task, true); 
				if(jobDetail != null) {
				   scheduler.addJob(jobDetail, true);
				   scheduler.triggerJob(jobDetail.getKey());
				   scheduler.deleteJob(jobDetail.getKey());
				   return true;
				}
			} else {
				scheduler.triggerJob(trigger.getJobKey());
				return true;
			}
		} catch (Exception e) {return false;}
		return false;
	}
	
	//构建执行器
	private CronTriggerImpl getCronTrigger(JobDetail jobDetail, Task task) throws ParseException {
		JobDataMap jobDataMap = new JobDataMap();
		Date startTime = new Date(System.currentTimeMillis());
		jobDataMap.put("jobDetail", jobDetail); // 固定写法
		CronTriggerImpl cti = new CronTriggerImpl();
		cti.setName(TASK_PREFIX + task.getId());
		cti.setGroup(Scheduler.DEFAULT_GROUP);
		cti.setJobKey(jobDetail.getKey());
		cti.setJobDataMap(jobDataMap);
		cti.setStartTime(startTime);
		cti.setCronExpression(task.getCronExpression());
		cti.setTimeZone(TimeZone.getDefault());
		cti.setPriority(1); // 优先级别
		return cti;
	}
	
	//构建任务
	private JobDetail getJobDetail(Task task, boolean storeDurably) {
		if (StringUtils.isBlank(task.getBusinessObject()) || (this.getExecutor(task)) == null
				|| !this.isValidExpression(task)) {
			return null;
		}
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(TaskExecutorAdapter.JOB_TASK_KEY, task.getId());
		Class<? extends TaskExecutorAdapter> jobClass = task.getConcurrent() ? TaskExecutorAdapter.class : StatefulTaskExecutorAdapter.class;
		JobBuilder builder = JobBuilder.newJob(jobClass);
		builder.usingJobData(jobDataMap);
		builder.withIdentity(new StringBuilder(TASK_PREFIX).append(task.getId()).toString());
		if (storeDurably) {
		    builder.storeDurably();
		}
		return builder.build();
	}
	
    //下一次执行的时间点(主要校验执行时间是否填写正确)
	public boolean isValidExpression(Task task) {
		return CronExpression.isValidExpression(task.getCronExpression());
	}
	
	// 具体的任务执行器
	public TaskExecutor getExecutor(Task task){
		try{
			return SpringContextHolder.getBean(task.getBusinessObject());
		}catch(Exception e) {
			return null;
		}
	}
}