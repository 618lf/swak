package com.swak.vertx.protocol.http;

import com.swak.meters.MetricsFactory;

/**
 * 路由执行器 -- 路由最终对应的代码(目前没用到)
 * 
 * @author lifeng
 * @date 2020年4月2日 下午2:55:09
 */
public interface HandlerInvoker {

	/**
	 * 设置监控
	 *
	 * @param metricsFactory 指标框架
	 */
	void applyMetrics(MetricsFactory metricsFactory);
	
	/**
	 * 调用
	 *
	 * @param args 参数
	 * @return 直接结果
	 */
	Object doInvoke(Object[] args) throws Throwable;
}
