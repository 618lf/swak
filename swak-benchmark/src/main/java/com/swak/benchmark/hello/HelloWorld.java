package com.swak.benchmark.hello;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.Throughput)//基准测试类型  
@OutputTimeUnit(TimeUnit.SECONDS)//基准测试结果的时间类型  
@Warmup(iterations = 3)//预热的迭代次数  
@Threads(2)//测试线程数量  
@State(Scope.Thread)//该状态为每个线程独享  
//度量:iterations进行测试的轮次，time每轮进行的时长，timeUnit时长单位,batchSize批次数量  
@Measurement(iterations = 2, time = -1, timeUnit = TimeUnit.SECONDS, batchSize = -1)  
public class HelloWorld {

	@Benchmark
	public void wellHelloThere() {

	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(HelloWorld.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}
}
