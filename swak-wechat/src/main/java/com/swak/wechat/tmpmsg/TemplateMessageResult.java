package com.swak.wechat.tmpmsg;

import java.io.Serializable;

/**
 * 模板消息结果
 *
 * @author: lifeng
 * @date: 2020/4/1 11:35
 */
public class TemplateMessageResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String errcode;
    private String errmsg;
    private Long msgid;

    public Long getMsgid() {
        return msgid;
    }

    public void setMsgid(Long msgid) {
        this.msgid = msgid;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}