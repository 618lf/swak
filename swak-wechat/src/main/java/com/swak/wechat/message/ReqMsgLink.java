package com.swak.wechat.message;

import org.w3c.dom.Element;

import com.swak.utils.XmlParse;

/**
 * 链接请求消息
 *
 * @author lifeng
 */
public class ReqMsgLink extends AbstractReqMsg {

    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private String url;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void read(Element element) {
        super.read(element);
        this.title = XmlParse.elementText(element, "Title");
        this.description = XmlParse.elementText(element, "Description");
        this.url = XmlParse.elementText(element, "Url");
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("msgId:").append(this.getMsgId()).append("\n");
        msg.append("title:").append(this.getTitle()).append("\n");
        msg.append("description:").append(this.getDescription()).append("\n");
        msg.append("url:").append(this.getUrl()).append("\n");
        return msg.toString();
    }
}
