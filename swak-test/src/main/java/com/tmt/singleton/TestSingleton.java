package com.tmt.singleton;

public class TestSingleton {

	public static void main(String[] args) {
		TestFactory test = TestFactory.INSTANCE;
		test.doTest();
		test.doTest("111");
	}
}

/**
 * 这种方式不错
 * @author lifeng
 *
 */
class TestFactory2 implements Test {

	private TestFactory2() {};
	
	static class Holder {
		static TestFactory2 instance = new TestFactory2();
	}
	
	static TestFactory2 getInstance() {
		return Holder.instance;
	}
	
	@Override
	public void doTest(String text) {
	}
}

/**
 * 奇葩的设计
 * @author lifeng
 *
 */
enum TestFactory implements Test {
	INSTANCE;

	@Override
	public void doTest(String text) {
		System.out.println("do text:" + text);
	}
}

interface Test {

	/**
	 * 直接在接口中实现
	 */
	default void doTest() {
		System.out.println("do Test");
	}
	
	void doTest(String text);
}