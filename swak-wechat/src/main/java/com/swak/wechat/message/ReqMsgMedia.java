package com.swak.wechat.message;

import com.swak.utils.XmlParse;
import org.w3c.dom.Element;

/**
 * 多媒体请求消息的公共类。
 *
 * @author lifeng
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
