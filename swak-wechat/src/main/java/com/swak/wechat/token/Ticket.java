package com.swak.wechat.token;

import java.io.Serializable;

/**
 * JS 票据
 * 
 * @author lifeng
 */
public class Ticket implements Serializable, ExpireAble {

	private static final long serialVersionUID = 1L;
	private String errcode;
	private String errmsg;
	private String ticket;
	private Integer expires_in;
	private Long addTime;

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

	public Long getAddTime() {
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

	public void setAddTime(Long addTime) {
		this.addTime = addTime;
	}
}
