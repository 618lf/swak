package com.swak.rx.v2;

import java.util.concurrent.TimeUnit;

import com.swak.rx.Data;

/**
 * 使用回调的方式
 * 
 * @author lifeng
 * @date 2020年7月19日 下午10:35:35
 */
public class ApiImpl implements Api {

	@Override
	public void get(GetCallBack callBack) {
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("获得数据");
		Data data = new Data();
		callBack.onSucess(data);
	}

	@Override
	public void save(Data data, SaveCallBack callBack) {
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("存储数据");
		callBack.onSucess();
	}
}