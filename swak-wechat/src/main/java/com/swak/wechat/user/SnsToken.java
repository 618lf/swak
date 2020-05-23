package com.swak.wechat.user;

import java.io.Serializable;
import java.util.Date;

/**
 * 网页授权登录
 *
 * @author: lifeng
 * @date: 2020/4/1 11:37
 */
public class SnsToken implements Serializable {

    private static final long serialVersionUID = -1L;

    private String errcode;
    private String errmsg;
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String openid;
    private String unionid;
    private String scope;
    private String appId;
    private Date createDate;

    // 存储的信息
    private String code;
    private String state;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String accessToken) {
        access_token = accessToken;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expiresIn) {
        expires_in = expiresIn;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refreshToken) {
        refresh_token = refreshToken;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}