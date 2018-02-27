package com.tmt.concurrent;

public class Test {

	public static void main(String[] args) {
		DateTestFactory factory = new DateTestFactory();
		DateTest[] dts = new DateTest[100];
		for (int i = 0; i < dts.length; i++) {
			dts[i] = factory.getProxyInstance();
		}
		// 遍历执行
		for (DateTest dt : dts) {
			System.out.println(dt.getDate());
		}
	}
}
