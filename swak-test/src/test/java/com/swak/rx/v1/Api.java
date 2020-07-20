package com.swak.rx.v1;

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
	Data get();

	/**
	 * 存储到本地
	 * 
	 * @param data
	 */
	void save(Data data);

}