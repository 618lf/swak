package com.tmt.rx.my.v5;

import com.tmt.rx.my.LocationBean;
import com.tmt.rx.my.v3.CallBack;

public class LocationHelper {

	private ApiWrapper api;
	private static LocationHelper helper = new LocationHelper();

	private LocationHelper() {
		api = new ApiWrapper();
	}

	public static LocationHelper getHelper() {
		return helper;
	}

	/**
	 * 这就是 RxJava 的原理
	 * @param address
	 * @return
	 */
	AsynJob<Void> commit(final String address) {
		return api.getLocation(address).map(new Func<LocationBean, AsynJob<Void>>() {
			@Override
			public AsynJob<Void> call(LocationBean t) {
				return api.submitLocation(t);
			}
		});
	}
	
	public static void main(String[] args) {
		LocationHelper.getHelper().commit("111").start(new CallBack<Void>() {
			@Override
			public void onResult(Void result) {
				System.out.println("111");
			}
			
			@Override
			public void onError() {
				
			}
		});
	}
}
