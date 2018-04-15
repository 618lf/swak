package com.tmt.rx.my.v2;

import com.tmt.rx.my.LocationBean;

public interface Api {

	 /**  
     * 将输入的地址反地理编码成经纬度  
     * @param address  
     * @param getCallBack  
     */  
    void getLocation(String address, LocationCallBack getCallBack);  
    /**  
     * 将经纬度提交给服务器  
     * @param submitCallBack  
     */  
    void submitLocation(LocationBean bean, SubmitCallBack submitCallBack);  
      
    /**  
     * 获取地理位置回调  
     * @author tomliang  
     *  
     */  
    interface LocationCallBack{  
        void onLocationReceived(LocationBean bean);  
        void onError();  
    }  
    /**  
     * 提交位置回调  
     * @author tomliang  
     *  
     */  
    interface SubmitCallBack{  
        void onSubmitReceived();  
        void onError();  
    }  
}
