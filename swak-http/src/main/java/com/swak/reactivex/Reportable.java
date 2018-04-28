package com.swak.reactivex;

import com.codahale.metrics.MetricRegistry;

/**
 * 需要监控的对象
 * @author lifeng
 *
 */
public interface Reportable {

	/**
	 * 注册上报
	 */
	default void report(MetricRegistry registry) {}
}