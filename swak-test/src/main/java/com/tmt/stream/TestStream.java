package com.tmt.stream;

import java.util.stream.Stream;

public class TestStream {

	public static void main(String[] args) {
		Stream.iterate(0, i -> i+1).limit(10).forEach(i -> {
			System.out.println(i);
		});
	}
}
