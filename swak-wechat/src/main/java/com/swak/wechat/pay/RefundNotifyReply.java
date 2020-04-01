package com.swak.wechat.pay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;

/**
 * 支付通知返回结果
 *
 * @author: lifeng
 * @date: 2020/4/1 11:31
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundNotifyReply {

    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String return_code;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String return_msg;

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    /**
     * 退款成功
     *
     * @return
     */
    public static RefundNotifyReply success() {
        RefundNotifyReply reply = new RefundNotifyReply();
        reply.setReturn_code("SUCCESS");
        reply.setReturn_msg("OK");
        return reply;
    }

    /**
     * 退款失败
     *
     * @return
     */
    public static RefundNotifyReply fail() {
        RefundNotifyReply reply = new RefundNotifyReply();
        reply.setReturn_code("FAIL");
        reply.setReturn_msg("FAIL");
        return reply;
    }

}
