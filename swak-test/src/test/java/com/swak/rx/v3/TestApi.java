package com.swak.rx.v3;

import com.swak.rx.Data;

public class TestApi {

	public static void main(String[] args) {
		Api api = new ApiImpl();
		ApiProxy apiProxy = new ApiProxy(api);

		apiProxy.get().then(new CallBack<Data>() {
			@Override
			public void onSucess(Data data) {
				apiProxy.save(data).then(new CallBack<Void>() {

					@Override
					public void onSucess(Void data) {
						System.out.println("收到成功了");
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
