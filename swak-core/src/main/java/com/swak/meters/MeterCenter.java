package com.swak.meters;

import com.swak.utils.Lists;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;

/**
 * 用于统计系统指标 -- 只有加载了 actuator 才会生效
 * @author lifeng
 */
public class MeterCenter implements MeterBinder{

	// 不需要其他地方来初始化
	private static MeterCenter instance = null;
	private MeterRegistry registry;
	public MeterCenter() {
		instance = this;
	}
	private MeterRegistry registry() {
		return registry;
	}
	
	@Override
	public void bindTo(MeterRegistry registry) {
        this.registry = registry;	
	}
	
	/**
	 * 统计 一段代码被调用的速率和用时。
	 * @param name
	 * @param tags
	 */
	public static void timer(String name, String ...tags) {
		if (instance != null) {
			instance.registry().timer(name, tags);
		}
	}
	
	/**
	 * 统计 一个指标的当前数量 ，例如：在线的 channel 数量
	 * @param name
	 * @param tags
	 */
	public static void counter(String name, String ...tags) {
		if (instance != null) {
			instance.registry().counter(name, tags);
		}
	}
	
	/**
	 * 代表一个度量的即时值。 当你开汽车的时候， 当前速度是Gauge值。 你测体温的时候， 体温计的刻度是一个Gauge值。 
	 * 当你的程序运行的时候， 内存使用量和CPU占用率都可以通过Gauge值来度量
	 * @param name
	 * @param tags
	 */
	public static void gauge(String name, Integer number, Tag ... tags) {
		if (instance != null) {
			instance.registry().gauge(name, Lists.newArrayList(tags), number);
		}
	}
	
	/**
	 * 测试样品的分布情况
	 * @param name
	 * @param tags
	 */
	public static void summary(String name, String ... tags) {
		if (instance != null) {
			instance.registry().summary(name, tags);
		}
	}
}