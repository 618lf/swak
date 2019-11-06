package com.swak.wechat.message;

import javax.xml.bind.annotation.XmlElement;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;

/**
 * 弹出微信相册发图器的事件推送 pic_photo_or_album 或 pic_weixin 或 pic_sysphoto
 */
public class MenuEventMsgPicWeuxin extends MenuEventMsg {

	private static final long serialVersionUID = 1L;

	@XmlElement(name="Count")
	private String count;

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
	@Override
	public void read(Element element) {
		this.count = XmlParse.elementText(element, "Count");
	}
}
