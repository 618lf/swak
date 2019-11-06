package com.swak.wechat.message;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;

/**
 * 请求消息基类 -- 读取xml中的数据
 * 
 * @author lifeng
 */
public abstract class ReqMsg extends MsgHead {

	private static final long serialVersionUID = 1L;
	
	protected String msgId;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
	public String getShowMessage() {return "";};
	
	@Override
	public void read(Element element) {
		this.msgId = XmlParse.elementText(element, "MsgId");
	}
}
