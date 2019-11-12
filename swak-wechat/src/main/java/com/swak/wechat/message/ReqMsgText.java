package com.swak.wechat.message;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;

/**
 * 文本请求消息
 */
public class ReqMsgText extends AbstractReqMsg {

	private static final long serialVersionUID = 1L;
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void read(Element element) {
		super.read(element);
		this.content = XmlParse.elementText(element, "Content");
	}

	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("msgId:").append(this.getMsgId()).append("\n");
		msg.append("content:").append(this.getContent()).append("\n");
		return msg.toString();
	}
}
