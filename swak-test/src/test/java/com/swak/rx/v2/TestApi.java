package com.swak.rx.v2;

import com.swak.rx.Data;
import com.swak.rx.v2.Api.GetCallBack;
import com.swak.rx.v2.Api.SaveCallBack;

public class TestApi {

	public static void main(String[] args) {
		Api api = new ApiImpl();

		api.get(new GetCallBack() {
			@Override
			public void onSucess(Data data) {
				System.out.println("获取数据成功");
				api.save(data, new SaveCallBack() {

					@Override
					public void onSucess() {
						System.out.println("存储数据成功");
					}

					@Override
					public void onError(Throwable e) {

					}
				});
			}

			@Override
			public void onError(Throwable e) {
			}
		});
	}
}
