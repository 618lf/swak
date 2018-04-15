package com.tmt.rx.my.v3;

import com.tmt.rx.my.LocationBean;
import com.tmt.rx.my.v2.Api;
import com.tmt.rx.my.v2.Api.LocationCallBack;
import com.tmt.rx.my.v2.Api.SubmitCallBack;
import com.tmt.rx.my.v2.DefaultApi;

public class ApiWrapper {
	Api api;

	public ApiWrapper() {
		api = new DefaultApi();
	}
	
	public void getLocation(String address, final CallBack<LocationBean> callback){  
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
	
	public void submitLocation(LocationBean bean, final CallBack<Void> callback){  
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
}
