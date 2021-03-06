package com.swak.wechat.token;

import java.io.Serializable;

/**
 * 接入 token
 *
 * @author: lifeng
 * @date: 2020/4/1 11:36
 */
public class AccessToken implements Serializable, ExpireAble {

    private static final long serialVersionUID = 1L;
    private String errcode;
    private String errmsg;
    private String access_token;
    private Long addTime;
    private Integer expires_in;

    public String getErrcode() {
        return errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public String getAccess_token() {
        return access_token;
    }

    @Override
	public Long getAddTime() {
        return addTime;
    }

    @Override
	public Integer getExpires_in() {
        return expires_in;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }
}
