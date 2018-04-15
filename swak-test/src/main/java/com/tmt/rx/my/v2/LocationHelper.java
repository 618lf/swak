package com.tmt.rx.my.v2;

import com.tmt.rx.my.LocationBean;
import com.tmt.rx.my.v2.Api.LocationCallBack;
import com.tmt.rx.my.v2.Api.SubmitCallBack;

/**
 * 典型的回调式方式，
 * 每一步操作都需要定义一个回调接口
 * @author lifeng
 */
public class LocationHelper {

    private Api api;  
    private static LocationHelper helper = new LocationHelper();  
      
    private LocationHelper() {  
        api = new DefaultApi();  
    }  
      
    public static LocationHelper getHelper(){  
        return helper;  
    }  
      
    void commit(String address, final CommitCallBack callback) {
        api.getLocation(address, new LocationCallBack() {  
                  
            @Override  
            public void onLocationReceived(LocationBean bean) {  
                api.submitLocation(bean, new SubmitCallBack() {  
                      
                    @Override  
                    public void onSubmitReceived() {  
                        callback.onCommitReceived();  
                    }  
                      
                    @Override  
                    public void onError() {  
                        callback.onError();  
                    }  
                });  
            }  
                  
            @Override  
            public void onError() {  
                callback.onError();  
            }  
        });  
    }  
  
    public interface CommitCallBack{  
        void onCommitReceived();  
        void onError();  
    }  
}
