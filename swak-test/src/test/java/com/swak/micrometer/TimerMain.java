package com.swak.micrometer;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class TimerMain {
	private static final Random R = new Random();

	static {
		Metrics.addRegistry(new SimpleMeterRegistry());
	}

	public static void main(String[] args) throws Exception {
		Order order1 = new Order();
		order1.setOrderId("ORDER_ID_1");
		order1.setAmount(100);
		order1.setChannel("CHANNEL_A");
		order1.setCreateTime(LocalDateTime.now());
		Timer timer = Metrics.timer("timer", "createOrder", "cost");
		timer.record(() -> createOrder(order1));
	}

	private static void createOrder(Order order) {
		try {
			TimeUnit.SECONDS.sleep(R.nextInt(5)); //模拟方法耗时
		} catch (InterruptedException e) {
		}
	}
}
