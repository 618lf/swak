package com.swak.wechat.message;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;

/**
 * 视频请求消息
 *
 * @author: lifeng
 * @date: 2020/4/1 11:26
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
		return "msgId:" + this.getMsgId() + "\n" +
				"thumbMediaId:" + this.getThumbMediaId() + "\n";
    }
}
