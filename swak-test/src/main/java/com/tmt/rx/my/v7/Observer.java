package com.tmt.rx.my.v7;

/**
 * 观察者
 * @author lifeng
 *
 * @param <T>
 */
public interface Observer<T> {
	void onCompleted();
    void onError(Throwable t);
    void onNext(T var1);
}
