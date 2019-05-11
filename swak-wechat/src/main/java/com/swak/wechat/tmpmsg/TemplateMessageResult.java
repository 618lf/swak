package com.swak.wechat.tmpmsg;

import java.io.Serializable;

public class TemplateMessageResult implements Serializable {

	private static final long serialVersionUID = -3936043437027443795L;
	
	private String errcode;
	private String errmsg;
	private Long msgid;
	
	public Long getMsgid() {
		return msgid;
	}
	public void setMsgid(Long msgid) {
		this.msgid = msgid;
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
}