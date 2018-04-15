package com.tmt.rx.my.v6;

public class TestAsyncJob {

	public static void main(String[] args) {

		new AsynJob<String>() {
			@Override
			public String call(CallBack<String> callBack) {
				String result = "123";
				System.out.println("1");
				callBack.onResult(result);
				return result;
			}
		}.map((result) -> {
			return new AsynJob<Integer>() {
				@Override
				public Integer call(CallBack<Integer> callBack) {
					Integer _result = Integer.parseInt(result);
					System.out.println("2");
					callBack.onResult(_result);
					return _result;
				}
			};
		}).map((result) -> {
			return new AsynJob<Long>() {
				@Override
				public Long call(CallBack<Long> callBack) {
					Long _result = result.longValue();
					System.out.println("3");
					callBack.onResult(_result);
					return _result;
				}
			};
		}).call(new CallBack<Long>() {
			@Override
			public void onResult(Long result) {
				System.out.println("获得数据" + result);
			}
			@Override
			public void onError() {
				
			}
		});
	}
}
