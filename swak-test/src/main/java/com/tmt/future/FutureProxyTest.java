package com.tmt.future;

/**
 * 这段代码有点意思，
 * 有时候需要通过简单的设置能异步获取数据。
 * 隐藏了异步的逻辑
 * @author lifeng
 *
 */
public class FutureProxyTest {

	public static void main(String[] args) {
		
		/**
		 * 1. 会异步执行 fetch();
		 * 2. 并等待 fetch() 执行完成
		 */
		AsyncData<String> myData = FutureProxy.proxy(new AsyncMyData());
		System.out.println(myData.get());
	}
}

/**
 * 非线程安全
 * @author lifeng
 */
class AsyncMyData implements AsyncData<String> {

	private String data;

	/**
	 * 会异步执行这段代码
	 */
	@Override
	public AsyncData<String> fetch() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("我获得了数据");
		data = "我是数据";
		return this;
	}

	/**
	 * 等待 fetch 执行完成
	 */
	@Override
	public String get() {
		return data;
	}
}
