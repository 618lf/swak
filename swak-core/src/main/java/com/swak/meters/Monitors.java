package com.swak.meters;

import com.swak.utils.Lists;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * 用于统计系统指标 -- 只有加载了 actuator 才会生效
 * 
 * @see SimpleMeterRegistry 但是这些数据不会发布到其他系统，也就是数据是位于应用的内存中的
 * @see CompositeMeterRegistry 多个MeterRegistry聚合，内部维护了一个MeterRegistry的列表
 * @see Metrics 中持有一个静态final的CompositeMeterRegistry实例globalRegistry
 * @author lifeng
 */
public class Monitors implements MeterBinder {

	// 不需要其他地方来初始化
	private static Monitors instance = null;
	private MeterRegistry registry;

	private MeterRegistry registry() {
		return registry;
	}

	@Override
	public void bindTo(MeterRegistry registry) {
		this.registry = registry;
	}
    
	/**
	 * 返回唯一的指标工具，需要使用 actuator 才会生效
	 * @return
	 */
	public static Monitors me() {
		if (instance == null) {
			instance = new Monitors();
		}
		return instance;
	}

	/**
	 * 统计 一段代码被调用的速率和用时。
	 * 
	 * @param name
	 * @param tags
	 */
	public static void timer(String name, String... tags) {
		if (instance != null) {
			instance.registry().timer(name, tags);
		}
	}

	/**
	 * 统计 一个指标的当前数量 ，例如：在线的 channel 数量
	 * 
	 * @param name
	 * @param tags
	 */
	public static void counter(String name, String... tags) {
		if (instance != null) {
			instance.registry().counter(name, tags).increment();
		}
	}

	/**
	 * 代表一个度量的即时值。 当你开汽车的时候， 当前速度是Gauge值。 你测体温的时候， 体温计的刻度是一个Gauge值。 当你的程序运行的时候，
	 * 内存使用量和CPU占用率都可以通过Gauge值来度量
	 * 
	 * @param name
	 * @param tags
	 */
	public static void gauge(String name, Integer number, Tag... tags) {
		if (instance != null) {
			instance.registry().gauge(name, Lists.newArrayList(tags), number);
		}
	}

	/**
	 * 测试样品的分布情况
	 * 
	 * @param name
	 * @param tags
	 */
	public static void summary(String name, String... tags) {
		if (instance != null) {
			instance.registry().summary(name, tags);
		}
	}
}