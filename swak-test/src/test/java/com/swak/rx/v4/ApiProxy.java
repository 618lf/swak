package com.swak.rx.v4;

import com.swak.rx.Data;
import com.swak.rx.v4.Api.GetCallBack;
import com.swak.rx.v4.Api.SaveCallBack;

/**
 * Api 代理层:
 * 问题： 
 * 
 * @author lifeng
 * @date 2020年7月19日 下午10:47:40
 */
public class ApiProxy {

	Api api;

	public ApiProxy(Api api) {
		this.api = api;
	}

	/**
	 * 封装成异步结果
	 * 
	 * @return
	 */
	public AsynJob<Data> get() {
		return new AsynJob<Data>() {
			@Override
			public void then(CallBack<Data> callback) {
				api.get(new GetCallBack() {
					@Override
					public void onSucess(Data data) {
						System.out.println("获取数据成功");
						callback.onSucess(data);
					}

					@Override
					public void onError(Throwable e) {
						callback.onError(e);
					}
				});
			}
		};
	}
	
	/**
	 * 封装成异步结果
	 * 
	 * @return
	 */
	public AsynJob<Void> save(Data data) {
		return new AsynJob<Void>() {
			@Override
			public void then(CallBack<Void> callback) {
				api.save(data, new SaveCallBack() {

					@Override
					public void onSucess() {
						System.out.println("存储数据成功");
						callback.onSucess(null);
					}

					@Override
					public void onError(Throwable e) {
						callback.onError(e);
					}
				});
			}
		};
	}
}