package com.tmt.reactor;

import reactor.core.publisher.Flux;

/**
 * 测试方法
 * 
 * @author lifeng
 */
public class TestMain {

	public static void main(String[] args) {
		Flux.just(1, 2).map(x -> 1 / x).log("Range").subscribe(System.out::println);
	}
}