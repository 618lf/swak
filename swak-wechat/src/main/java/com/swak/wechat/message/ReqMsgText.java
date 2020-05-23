package com.swak.wechat.message;

import com.swak.utils.XmlParse;
import org.w3c.dom.Element;

/**
 * 文本请求消息
 *
 * @author: lifeng
 * @date: 2020/4/1 11:26
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
        return "msgId:" + this.getMsgId() + "\n" +
                "content:" + this.getContent() + "\n";
    }
}
