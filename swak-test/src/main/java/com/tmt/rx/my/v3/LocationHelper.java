package com.tmt.rx.my.v3;

import com.tmt.rx.my.LocationBean;

public class LocationHelper {

	private ApiWrapper api;
	private static LocationHelper helper = new LocationHelper();
	private LocationHelper() {  
        api = new ApiWrapper();  
    }  
      
    public static LocationHelper getHelper(){  
        return helper;  
    }  
    
    void commit(String address, final CallBack<Void> callback){  
        api.getLocation(address, new CallBack<LocationBean>() {  
              
            @Override  
            public void onResult(LocationBean result) {  
                api.submitLocation(result, callback);  
            }  
              
            @Override  
            public void onError() {  
                callback.onError();  
            }  
        });  
    }  
}
