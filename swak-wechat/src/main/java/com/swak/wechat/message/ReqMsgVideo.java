package com.swak.wechat.message;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;

/**
 * 视频请求消息
 * 
 * @author rikky.cai
 * @qq:6687523
 * @Email:6687523@qq.com
 * 
 */
public class ReqMsgVideo extends ReqMsgMedia {

	private static final long serialVersionUID = 1L;
	
	private String thumbMediaId;

	public String getThumbMediaId() {
		return thumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}
	
	@Override
	public void read(Element element) {
		super.read(element);
		this.thumbMediaId = XmlParse.elementText(element, "thumbMediaId");
	}

	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("msgId:").append(this.getMsgId()).append("\n");
		msg.append("thumbMediaId:").append(this.getThumbMediaId()).append("\n");
		return msg.toString();
	}
}
