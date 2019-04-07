package com.swak.wechat.base;

import java.io.Serializable;
import java.util.Date;

/**
 * 接入 token 
 * 
 * @author lifeng
 */
public class AccessToken implements Serializable {

	private static final long serialVersionUID = 1L;
	private String errcode;
	private String errmsg;
	private String access_token;
	private Date addTime;
	private int expires_in;
	
	public String getErrcode() {
		return errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public String getAccess_token() {
		return access_token;
	}
	public Date getAddTime() {
		return addTime;
	}
	public int getExpires_in() {
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
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
}
