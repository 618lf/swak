package com.swak.wechat.message;

import com.swak.utils.CDataAdapter;
import com.swak.utils.XmlParse;
import com.swak.utils.time.DateTimes;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

/**
 * 基本的消息头
 *
 * @author: lifeng
 * @date: 2020/4/1 11:25
 */
public class MsgHeadImpl implements MsgHead {

    private static final long serialVersionUID = 1L;

    protected String toUserName;
    protected String fromUserName;
    protected String createTime;
    protected String msgType;
    protected String event;

    public MsgHeadImpl() {
        this.createTime = DateTimes.getMilliByTime(LocalDateTime.now()).toString();
    }

    @Override
    @XmlElement(name = "ToUserName")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    @Override
    @XmlElement(name = "FromUserName")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    @Override
    @XmlElement(name = "CreateTime")
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    @XmlElement(name = "MsgType")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @Override
    @XmlElement(name = "Event")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * 设置头部
     *
     * @param head 消息头
     */
    public void setHead(MsgHead head) {
        this.toUserName = head.getToUserName();
        this.fromUserName = head.getFromUserName();
        this.createTime = head.getCreateTime();
        this.msgType = head.getMsgType();
        this.event = head.getEvent();
    }

    public void read(Element element) {
        this.toUserName = XmlParse.elementText(element, "ToUserName");
        this.fromUserName = XmlParse.elementText(element, "FromUserName");
        this.createTime = XmlParse.elementText(element, "CreateTime");
        this.msgType = XmlParse.elementText(element, "MsgType");
        this.event = XmlParse.elementText(element, "Event");
    }
}
