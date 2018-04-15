package com.tmt.rx.my.v2;

import com.tmt.rx.my.LocationBean;

public class DefaultApi implements Api{

	@Override
	public void getLocation(String address, LocationCallBack getCallBack) {
		LocationBean bean = new LocationBean();
		bean.lat = 1; bean.lon = 2;
		getCallBack.onLocationReceived(bean);
	}

	@Override
	public void submitLocation(LocationBean bean, SubmitCallBack submitCallBack) {
		submitCallBack.onSubmitReceived();
	}
}
