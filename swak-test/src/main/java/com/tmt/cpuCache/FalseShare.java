package com.tmt.cpuCache;

import io.netty.util.NettyRuntime;

public class FalseShare implements Runnable{

	public static int NUM_THREADS = NettyRuntime.availableProcessors();
	public static long RUN_TIMES = 500L * 1000L * 1000L;
	public static VolatileLong[] longs = new VolatileLong[NUM_THREADS];
	private int index;
	
	public FalseShare(int index) {
		this.index = index;
		longs[index] = new VolatileLong();
	}
	
	@Override
	public void run() {
		long i = RUN_TIMES + 1;
		while(0 != i--) {
			longs[index].set(i);
		}
	}
}
