package com.swak.loadbalance;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 需要做负载均衡的对象的包装
 * 
 * @author lifeng
 * @date 2020年4月30日 上午11:08:46
 */
public class Referer<T> {

	/**
	 * 真实的对象
	 */
	public T ref;

	/**
	 * 名称
	 */
	public String name;

	/**
	 * 激活的次数
	 */
	public AtomicInteger activeCount = new AtomicInteger(0);

	/**
	 * 获得名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 返回激活数
	 * 
	 * @return 激活数
	 */
	public int getActiveCount() {
		return activeCount.get();
	}

	/**
	 * 选择此数据源
	 * 
	 * @return
	 */
	public T select() {
		this.activeCount.getAndIncrement();
		return ref;
	}
}