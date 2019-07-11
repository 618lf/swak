package com.swak.meters;

/**
 * 
 * 创建系统指标库
 * 
 * @author lifeng
 */
public interface MetricsFactory {
	
	
	/**
	 * 返回实际的指标注册
	 * 
	 * @return
	 */
	<T> T metricRegistry();
    
	/**
	 * 线程池 PoolMetrics
	 * 
	 * @param name
	 * @param maxSize
	 * @return
	 */
	PoolMetrics<?> cteatePoolMetrics(String name, int maxSize);
	
	/**
	 * 定时任务 PoolMetrics
	 * 
	 * @param name
	 * @param maxSize
	 * @return
	 */
	PoolMetrics<?> createScheduleMetrics(String name, int maxSize);
}