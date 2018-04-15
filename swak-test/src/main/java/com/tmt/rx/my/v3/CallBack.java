package com.tmt.rx.my.v3;

/**
 * 提取一个公共的回调
 * @author lifeng
 * @param <T>
 */
public interface CallBack<T> {
	void onResult(T result);  
    void onError();  
}