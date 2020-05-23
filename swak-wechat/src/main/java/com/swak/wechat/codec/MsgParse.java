package com.swak.wechat.codec;

import com.swak.utils.XmlParse;
import com.swak.wechat.Constants;
import com.swak.wechat.message.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 消息解析
 *
 * @author lifeng
 */
public class MsgParse {

    /**
     * 直接利用 java dom 解析
     *
     * @param xml xml消息
     * @return 解析之后的请求
     */
    public static ReqMsg parseXml(String xml) {
        Document doc = XmlParse.parse(xml);
        Element root = doc.getDocumentElement();

        // 消息头
        MsgHeadImpl msgHead = new MsgHeadImpl();
        msgHead.read(root);

        // 请求消息
        ReqMsg msg = reqMsg(msgHead);

        // 获取数据
        if (msg instanceof MsgHeadImpl) {
            MsgHeadImpl msgImpl = (MsgHeadImpl) msg;
            msgImpl.setHead(msgHead);
            msgImpl.read(root);
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
