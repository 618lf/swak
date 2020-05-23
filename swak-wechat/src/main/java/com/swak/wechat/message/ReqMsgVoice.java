package com.swak.wechat.message;

import com.swak.utils.XmlParse;
import org.w3c.dom.Element;

/**
 * 语音请求消息及语音识别消息。
 * <p>
 * 开通语音识别功能，用户每次发送语音给公众号时，微信会在推送的语音消息XML数据包中，增加一个Recongnition字段。
 * 注：由于客户端缓存，开发者开启或者关闭语音识别功能，对新关注者立刻生效，对已关注用户需要24小时生效。 开发者可以重新关注此帐号进行测试。
 *
 * @author lifeng
 */
public class ReqMsgVoice extends ReqMsgMedia {

    private static final long serialVersionUID = 1L;

    private String format;
    private String recognition;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRecognition() {
        return recognition;
    }

    public void setRecognition(String recognition) {
        this.recognition = recognition;
    }

    @Override
    public void read(Element element) {
        super.read(element);
        this.format = XmlParse.elementText(element, "Format");
        this.recognition = XmlParse.elementText(element, "Recognition");
    }

    @Override
    public String toString() {
		return "msgId:" + this.getMsgId() + "\n" +
				"format:" + this.getFormat() + "\n" +
				"recognition:" + this.getRecognition() + "\n";
    }
}
