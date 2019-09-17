package com.swak.wechat.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;

import com.swak.utils.CDataAdapter;
import com.swak.utils.XmlParse;
import com.swak.utils.time.DateUtils;

/**
 * 消息头
 * 
 * @author lifeng
 */
public class MsgHead implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected String toUserName;
	protected String fromUserName;
	protected String createTime;
	protected String msgType;
	protected String event;

	public MsgHead() {
		this.createTime = String.valueOf(DateUtils.getTimeStampNow().getTime());
	}
	@XmlElement(name="ToUserName")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	@XmlElement(name="FromUserName")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	@XmlElement(name="CreateTime")
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@XmlElement(name="MsgType")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	@XmlElement(name="Event")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
	/**
	 * 设置头部
	 * @param head
	 */
	public void setHead(MsgHead head) {
		this.toUserName = head.getToUserName();  
		this.fromUserName = head.getFromUserName(); 
		this.createTime  = head.getCreateTime();
		this.msgType = head.getMsgType();
		this.event = head.getEvent();
	}
	
	public void read(Element element) {
		this.toUserName = XmlParse.elementText(element, "ToUserName");
		this.fromUserName = XmlParse.elementText(element, "FromUserName");
		this.createTime =  XmlParse.elementText(element, "CreateTime");
		this.msgType =  XmlParse.elementText(element, "MsgType");
		this.event =  XmlParse.elementText(element, "Event");
	}
}
