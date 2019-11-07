package com.swak.wechat.message;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;

/**
 * 模板消息
 * <xml>
		<ToUserName><![CDATA[gh_7f083739789a]]></ToUserName>
		<FromUserName><![CDATA[oia2TjuEGTNoeX76QEjQNrcURxG8]]></FromUserName>
		<CreateTime>1395658920</CreateTime>
		<MsgType><![CDATA[event]]></MsgType>
		<Event><![CDATA[TEMPLATESENDJOBFINISH]]></Event>
		<MsgID>200163836</MsgID>
		<Status><![CDATA[success]]></Status>
	</xml>
 * @author lifeng
 */
public class EventMsgTemplate extends MsgHeadImpl {

	private static final long serialVersionUID = 1L;
	
	private String msgId;
	private String status;
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public void read(Element element) {
		this.msgId = XmlParse.elementText(element, "MsgID");
		this.status = XmlParse.elementText(element, "Status");
	}
}
