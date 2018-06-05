package com.swak.reactivex.transport;

/**
 * 用于处理服务器的一些资源
 * @author lifeng
 */
public interface Disposable {


	/**
	 * 关闭
	 */
	void dispose();
	
	/**
	 * 是否关闭
	 * @return
	 */
	public default boolean isDisposed() {
		return false;
	}
}
