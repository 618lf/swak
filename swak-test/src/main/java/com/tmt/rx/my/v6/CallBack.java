package com.tmt.rx.my.v6;

/**
 * 提取一个公共的回调
 * @author lifeng
 * @param <T>
 */
public interface CallBack<T> {
	
	/**
	 * 成功后执行
	 * @param result
	 */
	void onResult(T result);  
	
	/**
	 * 失败后执行
	 */
    void onError();  
}
