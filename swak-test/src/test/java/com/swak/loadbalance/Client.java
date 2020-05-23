package com.swak.loadbalance;

import java.util.concurrent.atomic.AtomicInteger;

public class Client {

	public AtomicInteger count = new AtomicInteger();

	public void doSometing() {
		count.getAndIncrement();
	}
}
