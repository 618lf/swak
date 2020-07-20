package com.swak.rx.v4;

public class TestApi {

	public static void main(String[] args) {
		Api api = new ApiImpl();
		ApiProxy apiProxy = new ApiProxy(api);

		apiProxy.get().map(data -> {
			return apiProxy.save(data);
		}).then(new CallBack<Void>() {
			@Override
			public void onSucess(Void result) {
				System.out.println("最终成功");
			}

			@Override
			public void onError(Throwable e) {
			}
		});
	}
}