package com.swak.wechat.message;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;

/**
 * 地理位置消息请求
 * 
 * @author rikky.cai
 * @qq:6687523
 * @Email:6687523@qq.com
 * 
 */
public class ReqMsgLocation extends ReqMsg {

	private static final long serialVersionUID = 1L;
	
	private String location_X;
	private String location_Y;
	private String scale;
	private String label;

	public String getLocation_X() {
		return location_X;
	}

	public void setLocation_X(String locationX) {
		this.location_X = locationX;
	}

	public String getLocation_Y() {
		return location_Y;
	}

	public void setLocation_Y(String locationY) {
		this.location_Y = locationY;
	}

	public String getScale() {
		return scale;
	}

	public String getLabel() {
		return label;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public void read(Element element) {
		super.read(element);
		this.location_X = XmlParse.elementText(element, "Location_X");
		this.location_Y = XmlParse.elementText(element, "Location_Y");
		this.scale = XmlParse.elementText(element, "Scale");
		this.label = XmlParse.elementText(element, "Label");
	}

	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("msgId:").append(this.getMsgId()).append("\n");
		msg.append("location_X:").append(this.getLocation_X()).append("\n");
		msg.append("location_Y:").append(this.getLocation_Y()).append("\n");
		msg.append("scale:").append(this.getScale()).append("\n");
		msg.append("label:").append(this.getLabel()).append("\n");
		return msg.toString();
	}

	@Override
	public String getShowMessage() {
		StringBuilder msg = new StringBuilder();
		msg.append("<a data-type='pic' data-x='")
				.append(this.getLocation_X())
				.append("' data-y='" + this.getLocation_Y() + "' data-s='"
						+ this.getScale() + "' >" + this.getLabel() + "</a>");
		return msg.toString();
	}
}
