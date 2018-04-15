package com.tmt.rx.my.v4;

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

	AsynJob<LocationBean> commit(final String address) {
		return new AsynJob<LocationBean>() {
			@Override
			public void start(final CallBack<LocationBean> callback) {
				api.getLocation(address).start(new CallBack<LocationBean>() {
					@Override
					public void onResult(LocationBean result) {
						api.submitLocation(result).start(new CallBack<Void>() {
							@Override
							public void onResult(Void _result) {
								callback.onResult(result);
							}
							@Override
							public void onError() {
							}
						});
					}
					@Override
					public void onError() {
						
					}
				});
			}
		};
	}
	
	public static void main(String[] args) {
		LocationHelper.getHelper().commit("123").start(new CallBack<LocationBean>() {
			@Override
			public void onResult(LocationBean result) {
				System.out.println("我是最后的回调");
			}
			
			@Override
			public void onError() {
				
			}
		});;
	}
}
