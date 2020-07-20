package com.swak.rx.v4;

/**
 * 统一的回调接口
 * 
 * @author lifeng
 * @date 2020年7月19日 下午10:48:04
 */
public interface CallBack<T> {
	void onSucess(T data);

	void onError(Throwable e);
}