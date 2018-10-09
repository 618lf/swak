package com.swak.timer;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 任务
 * 
 * @author lifeng
 */
public class Task implements Serializable{
	
	private static final long serialVersionUID = -1301574146709137781L;
	private Long id;
	private String name;
    private String cronExpression;
    private Integer allowExecuteCount;
    private Integer yetExecuteCount;
    private Integer failExecuteCount;
    private Date preExecuteTime;
    private Date nextExecuteTime;
    private String businessObject;
    private String businessObjectName;
    private TaskStatus taskStatus;//任务的状态
    private Integer manualOperation;
    private Boolean concurrent = Boolean.FALSE; // 同一任务并行执行
    private String params; // 运行参数
    private String ops; // 操作
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOps() {
		return ops;
	}
	public void setOps(String ops) {
		this.ops = ops;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public Integer getAllowExecuteCount() {
		return allowExecuteCount;
	}
	public void setAllowExecuteCount(Integer allowExecuteCount) {
		this.allowExecuteCount = allowExecuteCount;
	}
	public Integer getYetExecuteCount() {
		return yetExecuteCount;
	}
	public void setYetExecuteCount(Integer yetExecuteCount) {
		this.yetExecuteCount = yetExecuteCount;
	}
	public Integer getFailExecuteCount() {
		return failExecuteCount;
	}
	public void setFailExecuteCount(Integer failExecuteCount) {
		this.failExecuteCount = failExecuteCount;
	}
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	public Date getPreExecuteTime() {
		return preExecuteTime;
	}
	public void setPreExecuteTime(Date preExecuteTime) {
		this.preExecuteTime = preExecuteTime;
	}
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	public Date getNextExecuteTime() {
		return nextExecuteTime;
	}
	public void setNextExecuteTime(Date nextExecuteTime) {
		this.nextExecuteTime = nextExecuteTime;
	}
	public String getBusinessObject() {
		return businessObject;
	}
	public void setBusinessObject(String businessObject) {
		this.businessObject = businessObject;
	}
	public String getBusinessObjectName() {
		return businessObjectName;
	}
	public void setBusinessObjectName(String businessObjectName) {
		this.businessObjectName = businessObjectName;
	}
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
	public String getTaskStatusName() {
		if(taskStatus != null) {
			return taskStatus.getName();
		}
		return "";
	}
	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
	public Integer getManualOperation() {
		return manualOperation;
	}
	public void setManualOperation(Integer manualOperation) {
		this.manualOperation = manualOperation;
	}
	public Boolean getConcurrent() {
		return concurrent;
	}
	public void setConcurrent(Boolean concurrent) {
		this.concurrent = concurrent;
	}
	
	/**
	 * 是否可以执行
	 * @return
	 */
	public Boolean needToRun(){
		if(this.getTaskStatus() != null && TaskStatus.RUNABLE == this.getTaskStatus()) {
		   return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public enum TaskStatus {
		NEW("待启动"),RUNABLE("等待执行"),RUNNING("执行中"),WAIT("暂停"),FINISH("结束");
		private String name;
		private TaskStatus(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}