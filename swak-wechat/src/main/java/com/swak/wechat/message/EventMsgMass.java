package com.swak.wechat.message;

import com.swak.utils.XmlParse;
import org.w3c.dom.Element;

/**
 * 群发消息
 *
 * @author: lifeng
 * @date: 2020/4/1 11:20
 */
public class EventMsgMass extends AbstractEventMsg {

    private static final long serialVersionUID = 1L;

    private String msgId;
    private String status;
    private String totalCount;
    private String filterCount;
    private String sentCount;
    private String errorCount;

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getFilterCount() {
        return filterCount;
    }

    public void setFilterCount(String filterCount) {
        this.filterCount = filterCount;
    }

    public String getSentCount() {
        return sentCount;
    }

    public void setSentCount(String sentCount) {
        this.sentCount = sentCount;
    }

    public String getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(String errorCount) {
        this.errorCount = errorCount;
    }

    @Override
    public void read(Element element) {
        this.msgId = XmlParse.elementText(element, "MsgID");
        this.status = XmlParse.elementText(element, "Status");
        this.totalCount = XmlParse.elementText(element, "TotalCount");
        this.filterCount = XmlParse.elementText(element, "FilterCount");
        this.sentCount = XmlParse.elementText(element, "SentCount");
        this.errorCount = XmlParse.elementText(element, "ErrorCount");
    }
}
