package com.tmt.future;

import org.junit.Test;

public class TestLambda {

	public String variable = "Class Level Variable";
	
	/**
	 * 执行的入口
	 * @param c
	 */
	public void doCall(Func c) {
		c.call();
	}
	
	@Test
	public void testAnonymous() {
		String variable = "Method Level Variable";
		
		/**
		 * 匿名内部类的变量作用域
		 * 方法的变量被隐藏了
		 */
		doCall(new Func() {
			@Override
			public void call() {
				// variable = ""; -- 不能改变值
				System.out.println(variable);
				System.out.println(TestLambda.this.variable);
			}
		});
	}
	
	/**
	 * Lambda 是没有this的问题
	 */
	@Test
	public void testLambda() {
		String variable = "Method Level Variable";
		this.doCall(()->{
			// String variable = "Lambda Level Variable";  // 不能创建同名的变量
			System.out.println(variable);
			System.out.println(this.variable);
		});
	}
}

/**
 * 即口
 * @author lifeng
 */
@FunctionalInterface
interface Func {
	void call();
}
