package com.swak.timer;

import org.quartz.DisallowConcurrentExecution;


/**
 * 任务执行适配器 --- 标识不能同时执行
 * @author root
 */
@DisallowConcurrentExecution
public class StatefulTaskExecutorAdapter extends TaskExecutorAdapter{}