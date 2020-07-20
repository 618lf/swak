package com.swak.rx.v3;

/**
 * 异步任务
 * 
 * @author lifeng
 * @date 2020年7月19日 下午10:50:33
 */
public interface AsynJob<T> {
	void then(CallBack<T> callback);
}