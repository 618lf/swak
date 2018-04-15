package com.tmt.rx.my.v1;

import com.tmt.rx.my.LocationBean;

/**  
* 操作接口  
* @author tomliang  
*/
public interface Api {

	 /**  
     * 将输入的地址反地理编码成经纬度  
     * @param address  
     * @return  
     */  
    LocationBean getLocation(String address);  
    /**  
     * 将经纬度提交给服务器  
     * @param bean  
     */  
    void submitLocation(LocationBean bean);  
}
