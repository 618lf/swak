package com.tmt.time;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class TimeTest {

	@Test
	public void start() {
		AtomicInteger count = new AtomicInteger();
		for(int i=0; i<5000000;i++) {
			count.incrementAndGet();
		}
	}
}
