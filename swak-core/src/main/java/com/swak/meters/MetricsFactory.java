package com.swak.meters;

import java.lang.reflect.Method;

/**
 * 创建系统指标库
 *
 * @author: lifeng
 * @date: 2020/3/29 12:05
 */
public interface MetricsFactory {

	/**
	 * 返回实际的指标注册
	 *
	 * @param <T> 类型
	 * @return 指标注册
	 */
	<T> T metricRegistry();

	/**
	 * 线程池 PoolMetrics
	 *
	 * @param name    指标名称
	 * @param maxSize pool 大小
	 * @return PoolMetrics
	 */
	PoolMetrics<?> createPoolMetrics(String name, int maxSize);

	/**
	 * 定时任务 PoolMetrics
	 *
	 * @param name    指标名称
	 * @param maxSize pool 大小
	 * @return PoolMetrics
	 */
	PoolMetrics<?> createScheduleMetrics(String name, int maxSize);

	/**
	 * 方法 MethodMetrics
	 *
	 * @param method 方法
	 * @param name   指标名称
	 * @return MethodMetrics
	 */
	MethodMetrics<?> createMethodMetrics(Method method, String name);

	/**
	 * Sql Metrics
	 *
	 * @param method 方法
	 * @param name   指标名称
	 * @return SqlMetrics
	 */
	SqlMetrics<?> createSqlMetrics(String sql);
}