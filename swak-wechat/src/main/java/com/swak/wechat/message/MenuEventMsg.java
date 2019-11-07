package com.swak.wechat.message;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;


/**
 * 菜单上的事件: 1 点击菜单拉取消息时的事件推送 2 点击菜单跳转链接时的事件推送 3 scancode_push：扫码推事件的事件推送 4
 * scancode_waitmsg：扫码推事件且弹出“消息接收中”提示框的事件推送 5 pic_sysphoto：弹出系统拍照发图的事件推送 6
 * pic_photo_or_album：弹出拍照或者相册发图的事件推送 7 pic_weixin：弹出微信相册发图器的事件推送 8
 * location_select：弹出地理位置选择器的事件推送
 * 
 * @author lifeng
 */
public class MenuEventMsg extends MsgHeadImpl {

	private static final long serialVersionUID = 1L;
	
	protected String eventKey;

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
	
	@Override
	public void read(Element element) {
		this.eventKey = XmlParse.elementText(element, "EventKey");
	}

	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("eventKey:").append(this.getEventKey());
		return msg.toString();
	}
}
