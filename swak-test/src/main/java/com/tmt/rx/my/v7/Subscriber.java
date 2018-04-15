package com.tmt.rx.my.v7;

/**
 * 抽象的观察者
 * @author lifeng
 * @param <T>
 */
public abstract class Subscriber<T> implements Observer<T>{

	/**
	 * 开始执行方法
	 */
	public void onStart() {}
}