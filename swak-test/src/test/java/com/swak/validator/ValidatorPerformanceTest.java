package com.swak.validator;

import com.swak.test.utils.MultiThreadTest;

/**
 * 正则验证基本需要耗费一多半的时间
 * 没有任何规则需要300ms的时间，研究下为什么，是否还可以优化
 * @author lifeng
 */
public class ValidatorPerformanceTest {

	public static void main(String[] args) {
		ValidatorTest test = new ValidatorTest();
		test.init();
		MultiThreadTest.run(() -> {
			for (int i = 0; i < 1000000; i++) {
				test.test();
			}
		}, 1, "验证性能测试 " + (1000000 * 1));
	}
}
