package com.swak.stream;

import java.util.stream.Stream;

public class Streams {

	public static void main(String[] args) {
		Stream.iterate(0, t -> t + 1).limit(2).forEach(t -> {
			System.out.println(t);
		});
	}
}
