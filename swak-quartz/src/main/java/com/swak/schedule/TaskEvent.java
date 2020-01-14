package com.swak.schedule;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 定时任务时间
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class TaskEvent {

	private String dispatch; // 调度号
	private String task; // 任务名称
	private Long nextTime;// 下次执行时间
}