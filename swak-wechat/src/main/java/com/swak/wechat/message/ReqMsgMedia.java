package com.swak.wechat.message;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;

/**
 * 多媒体请求消息的公共类。
 * 
 * @author rikky.cai
 * @qq:6687523
 * @Email:6687523@qq.com
 * 
 */
public abstract class ReqMsgMedia extends AbstractReqMsg {

	private static final long serialVersionUID = 1L;
	protected String mediaId;

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	@Override
	public void read(Element element) {
		super.read(element);
		this.mediaId = XmlParse.elementText(element, "MediaId");
	}

	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("mediaId:").append(this.getMediaId());
		return msg.toString();
	}
}
