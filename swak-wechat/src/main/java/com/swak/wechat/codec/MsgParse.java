package com.swak.wechat.codec;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.swak.utils.XmlParse;
import com.swak.wechat.Constants;
import com.swak.wechat.message.EventMsgLocation;
import com.swak.wechat.message.EventMsgMass;
import com.swak.wechat.message.EventMsgTemplate;
import com.swak.wechat.message.EventMsgUserAttention;
import com.swak.wechat.message.MenuEventMsgClick;
import com.swak.wechat.message.MenuEventMsgLocationSelect;
import com.swak.wechat.message.MenuEventMsgPicWeuxin;
import com.swak.wechat.message.MenuEventMsgScancodePush;
import com.swak.wechat.message.MenuEventMsgView;
import com.swak.wechat.message.MsgHead;
import com.swak.wechat.message.MsgHeadImpl;
import com.swak.wechat.message.ReqMsg;
import com.swak.wechat.message.ReqMsgImage;
import com.swak.wechat.message.ReqMsgLink;
import com.swak.wechat.message.ReqMsgLocation;
import com.swak.wechat.message.ReqMsgText;
import com.swak.wechat.message.ReqMsgVideo;
import com.swak.wechat.message.ReqMsgVoice;

/**
 * 消息解析
 * @author lifeng
 */
public class MsgParse {

	/**
	 * 直接利用 java dom 解析
	 * @param xml
	 * @return
	 */
	public static ReqMsg parseXML(String xml) {
		Document doc = XmlParse.parse(xml);
		Element root = doc.getDocumentElement();
		
		// 消息头
		MsgHeadImpl msgHead = new MsgHeadImpl();
		msgHead.read(root);
		
		// 请求消息
		ReqMsg msg = reqMsg(msgHead);
		
		// 获取数据
		if (msg != null && msg instanceof MsgHeadImpl) {
			MsgHeadImpl _msg = (MsgHeadImpl)msg;
			_msg.setHead(msgHead);
			_msg.read(root);
		}
		return msg;
	}
	
	private static ReqMsg reqMsg(MsgHead msgHead) {
		if (Constants.ReqType.text.name().equals(msgHead.getMsgType())) {
			return new ReqMsgText();
		} else if (Constants.ReqType.image.name().equals(msgHead.getMsgType())) {
			return new ReqMsgImage();
		} else if (Constants.ReqType.link.name().equals(msgHead.getMsgType())) {
			return new ReqMsgLink();
		} else if (Constants.ReqType.location.name().equals(msgHead.getMsgType())) {
			return new ReqMsgLocation();
		} else if (Constants.ReqType.video.name().equals(msgHead.getMsgType())
				|| Constants.ReqType.shortvideo.name().equals(msgHead.getMsgType())) {
			return new ReqMsgVideo();
		} else if (Constants.ReqType.voice.name().equals(msgHead.getMsgType())) {
			return new ReqMsgVoice();
		} else if (Constants.ReqType.event.name().equals(msgHead.getMsgType())) {
			return eventMsg(msgHead);
		}
		return null;
	}
	
	// 事件
	private static ReqMsg eventMsg(MsgHead msgHead) {
		if (Constants.EventType.CLICK.name().equals(msgHead.getEvent())) {
			return new MenuEventMsgClick();
		} else if (Constants.EventType.VIEW.name().equals(msgHead.getEvent())) {
			return new MenuEventMsgView();
		} else if (Constants.EventType.LOCATION.name().equals(msgHead.getEvent())) {
			return new EventMsgLocation();
		} else if (Constants.EventType.pic_weixin.name().equals(msgHead.getEvent())
				|| Constants.EventType.pic_photo_or_album.name().equals(msgHead.getEvent())
				|| Constants.EventType.pic_sysphoto.name().equals(msgHead.getEvent())) {
			return new MenuEventMsgPicWeuxin();
		} else if (Constants.EventType.scancode_push.name().equals(msgHead.getEvent())
				|| Constants.EventType.scancode_waitmsg.name().equals(msgHead.getEvent())) {
			return new MenuEventMsgScancodePush();
		} else if (Constants.EventType.location_select.name().equals(msgHead.getEvent())) {
			return new MenuEventMsgLocationSelect();
		} else if (Constants.EventType.SCAN.name().equals(msgHead.getEvent())
				|| Constants.EventType.subscribe.name().equals(msgHead.getEvent())
				|| Constants.EventType.unsubscribe.name().equals(msgHead.getEvent())) {
			return new EventMsgUserAttention();
		} else if (Constants.EventType.TEMPLATESENDJOBFINISH.name().equals(msgHead.getEvent())) {
			return new EventMsgTemplate();
		} else if (Constants.EventType.MASSSENDJOBFINISH.name().equals(msgHead.getEvent())) {
			return new EventMsgMass();
		}
		
		// 其他的暂时不支持
		return null;
	}
}
