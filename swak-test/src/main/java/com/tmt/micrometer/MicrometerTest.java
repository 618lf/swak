package com.tmt.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class MicrometerTest {

	public static void main(String[] args) {
		MeterRegistry registry = new SimpleMeterRegistry();
		Counter compositeCounter = registry.counter("counter", "region", "test");
		compositeCounter.increment();
		System.out.println(compositeCounter.count());
		registry.close();
	}
}