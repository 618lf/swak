package com.swak.rx.v3;

import com.swak.rx.Data;

/**
 * API 服务类
 * 
 * @author lifeng
 * @date 2020年7月19日 下午10:27:04
 */
public interface Api {

	/**
	 * 从网络获取数据
	 * 
	 * @return
	 */
	void get(GetCallBack callBack);

	/**
	 * 存储到本地
	 * 
	 * @param data
	 */
	void save(Data data, SaveCallBack callBack);

	/**
	 * 回调接口
	 * 
	 * @author lifeng
	 * @date 2020年7月19日 下午10:33:34
	 */
	interface GetCallBack {

		void onSucess(Data data);

		void onError(Throwable e);
	}

	/**
	 * 回调接口
	 * 
	 * @author lifeng
	 * @date 2020年7月19日 下午10:33:34
	 */
	interface SaveCallBack {

		void onSucess();

		void onError(Throwable e);
	}
}