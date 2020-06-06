package com.swak.fetty.transport.promise;

/**
 * 结果监听
 * 
 * @author lifeng
 * @date 2020年6月4日 下午5:08:07
 */
public interface PromiseListener {

	/**
	 * 操作结束后回调
	 * 
	 * @param promise
	 * @throws Exception
	 */
	void operationComplete(ChannelPromise promise) throws Exception;
}