package com.tmt.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TestThreadPool {

	public static void main(String[] args) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		System.out.println(executor.getCorePoolSize());
		System.out.println(executor.getMaximumPoolSize());
		executor.setCorePoolSize(2); executor.setMaximumPoolSize(3);
		System.out.println(executor.getCorePoolSize());
		System.out.println(executor.getMaximumPoolSize());
	}
}
