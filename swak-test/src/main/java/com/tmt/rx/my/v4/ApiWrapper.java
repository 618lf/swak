package com.tmt.rx.my.v4;

import com.tmt.rx.my.LocationBean;
import com.tmt.rx.my.v2.Api;
import com.tmt.rx.my.v2.Api.LocationCallBack;
import com.tmt.rx.my.v2.Api.SubmitCallBack;
import com.tmt.rx.my.v2.DefaultApi;
import com.tmt.rx.my.v3.CallBack;

public class ApiWrapper {

	Api api;

	public ApiWrapper() {
		api = new DefaultApi();
	}

	public AsynJob<LocationBean> getLocation(final String address) {
		
		return new AsynJob<LocationBean>() {

			@Override
			public void start(final CallBack<LocationBean> callback) {
				api.getLocation(address, new LocationCallBack() {

					@Override
					public void onLocationReceived(LocationBean bean) {
						callback.onResult(bean);
					}

					@Override
					public void onError() {
						callback.onError();
					}
				});
			}
		};
	}

	public AsynJob<Void> submitLocation(final LocationBean bean) {
		return new AsynJob<Void>() {

			@Override
			public void start(final CallBack<Void> callback) {
				api.submitLocation(bean, new SubmitCallBack() {

					@Override
					public void onSubmitReceived() {
						callback.onResult(null);
					}

					@Override
					public void onError() {
						callback.onError();
					}
				});
			}
		};
	}
}
