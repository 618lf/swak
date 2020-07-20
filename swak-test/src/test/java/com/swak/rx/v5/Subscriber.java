package com.swak.rx.v5;

/**
 * 一个观察者
 * 
 * @author lifeng
 * @date 2020年7月20日 下午3:23:52
 */
public abstract class Subscriber<T> implements Observer<T> {

	/**
	 * 开始执行方法
	 */
	public void onStart() {
	}
}