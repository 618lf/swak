package com.swak.wechat.base;

import java.io.Serializable;
import java.util.Date;

/**
 * JS 票据
 * 
 * @author lifeng
 */
public class Ticket implements Serializable {

	private static final long serialVersionUID = 1L;
	private String errcode;
	private String errmsg;
	private String ticket;
	private Integer expires_in;
	private Date addTime;
	
	public String getErrcode() {
		return errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public String getTicket() {
		return ticket;
	}
	public Integer getExpires_in() {
		return expires_in;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public void setExpires_in(Integer expires_in) {
		this.expires_in = expires_in;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
}
