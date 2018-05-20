package com.swak.benchmark.hello;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Warmup(iterations = 3) // 预热的迭代次数
@OutputTimeUnit(TimeUnit.SECONDS)
@Measurement(iterations = 3, time = -1, timeUnit = TimeUnit.SECONDS, batchSize = -1)
@Threads(4)
public class HelloWorld {

	@State(Scope.Benchmark)
	public static class BenchmarkState {
		volatile double x = Math.PI;
	}
	
    @State(Scope.Thread)
    public static class ThreadState {
        volatile double x = Math.PI;
    }
    
    @Benchmark
    public void measureUnshared(ThreadState state) {
        state.x++;
    }
    
    @Benchmark
    public void measureShared(BenchmarkState state) {
        state.x++;
    }

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(HelloWorld.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}
}
