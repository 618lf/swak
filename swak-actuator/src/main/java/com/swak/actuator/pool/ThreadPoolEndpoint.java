package com.swak.actuator.pool;

import java.util.Map;

import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.actuator.endpoint.annotation.Selector;
import com.swak.executor.Workers;

/**
 * {@link Endpoint} to expose details of an netty PoolAllocator status context.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 * @since 2.0.0
 */
@Endpoint(id = "threadPool")
public class ThreadPoolEndpoint {

	/**
	 * 所有线程池的指标
	 * @return
	 */
	@Operation
	public Map<String, Object> metrics() {
		return Workers.executor.metrics();
	}
	
	/**
	 * 单个线程池的指标
	 * @return
	 */
	@Operation
	public Map<String, Object> metrics(@Selector String name) {
		return Workers.executor.metrics(name);
	}
	
	/**
	 * 设置指标
	 * @return
	 */
	@Operation
	public void poolSize(String name, int maxSize) {
		Workers.executor.poolSize(name, maxSize);
	}
	
	/**
	 * 设置指标
	 * @return
	 */
	@Operation
	public void coreSize(String name, int maxSize) {
		Workers.executor.coreSize(name, maxSize);
	}
	
	/**
	 * 设置指标
	 * @return
	 */
	@Operation
	public void timeSeconds(String name, int maxSize) {
		Workers.executor.timeSeconds(name, maxSize);
	}
}