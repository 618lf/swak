package com.swak.reactivex.transport;

/**
 * 用于处理服务器的一些资源
 * @author lifeng
 */
public interface Disposable {


	/**
	 * Cancel or dispose the underlying task or resource.
	 * <p>
	 * Implementations are required to make this method idempotent.
	 */
	void dispose();
	
	/**
	 * 
	 * @return
	 */
	public default boolean isDisposed() {
		return false;
	}
}
