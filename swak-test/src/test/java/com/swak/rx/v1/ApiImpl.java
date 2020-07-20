package com.swak.rx.v1;

import java.util.concurrent.TimeUnit;

import com.swak.rx.Data;

public class ApiImpl implements Api {

	@Override
	public Data get() {
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("获得数据");
		return new Data();
	}

	@Override
	public void save(Data data) {
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("存储数据");
	}
}
