package com.tmt.cpuCache;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * 测试 Share
 * 
 * @author lifeng
 */
public class TestFalseShareMain {

	@Test
	public void test() {
		Thread[] threads = new Thread[FalseShare.NUM_THREADS];
		Stream.iterate(0, i -> i + 1).limit(FalseShare.NUM_THREADS).forEach((i) -> {
			threads[i] = new Thread(new FalseShare(i));
			threads[i].start();
		});
		Arrays.stream(threads).forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
}