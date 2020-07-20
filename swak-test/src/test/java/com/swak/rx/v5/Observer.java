package com.swak.rx.v5;

/**
 * 观察者
 * 
 * @author lifeng
 * @date 2020年7月20日 下午3:22:54
 */
public interface Observer<T> {
	void onCompleted();

	void onError(Throwable t);

	void onNext(T var1);
}
