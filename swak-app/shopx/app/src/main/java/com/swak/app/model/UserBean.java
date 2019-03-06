package com.swak.app.model;

import com.veni.tools.RegTools;

import java.io.Serializable;

/**
 * 作者：kkan on 2017/12/04 10:36
 * 当前类注释:
 * 用户,根据实际情况来
 */

public class UserBean implements Serializable {
    private String userId;// 用户ID
    private String userName;// 用户名
    private String userType;//用户类型
    private String nickName;// 昵称
    private String phone;// 电话
    private String sex;// 性别
    private String address;//地区全名
    //        private String type;// 用户类型
    private String customerImg;//用户头像
    private String realName;//真实名字
    private String proviceId;
    private String proviceName;
    private String cityId;
    private String cityName;
    private String districtName;
    private String districtId;
    private String isCreator;//是否加入创建人 1代表 是创建者  0代表不是

    public String getUserId() {

        if (userId != null && RegTools.checkDecimals(userId)) {
            double uid = Double.parseDouble(userId);
            String nuid = userId;
            if (userId.contains(".")) {
                nuid = userId.substring(0, userId.indexOf("."));
                long nnuid=Long.parseLong(nuid);
                if(nnuid!=uid){
                    nuid = userId;
                }
            }
            return nuid + "";
        } else {
            return "";
        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerImg() {
        return customerImg == null ? "" : customerImg;
    }

    public void setCustomerImg(String customerImg) {
        this.customerImg = customerImg;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getProviceId() {
        return proviceId;
    }

    public void setProviceId(String proviceId) {
        this.proviceId = proviceId;
    }

    public String getProviceName() {
        return proviceName;
    }

    public void setProviceName(String proviceName) {
        this.proviceName = proviceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getIsCreator() {
        return isCreator;
    }

    public void setIsCreator(String isCreator) {
        this.isCreator = isCreator;
    }
}
