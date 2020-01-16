package com.swak.schedule;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * 定时任务
 * 
 * @author lifeng
 */
@Getter
@Setter
public class Task implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // 调度任务ID
	private String name; // 调度任务名称
	private String cronExpression; // 调度时间
	private Byte parallelAble; // 是否允许并行执行，不允许
	private String currDispatchNo; // 当前调度任务序号：有序号表示正在执行
	private LocalDateTime lastExecutionTime; // 最后执行时间
	private LocalDateTime nextExecutionTime; // 预计执行时间
	private Integer dispatchs; // 调度次数
	private Integer executions; // 执行次数
	private Integer maxExecutionTime;// 最长执行时间
}