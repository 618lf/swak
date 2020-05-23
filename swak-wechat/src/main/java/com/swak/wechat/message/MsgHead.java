package com.swak.wechat.message;

import java.io.Serializable;

/**
 * 消息头
 *
 * @author lifeng
 */
public interface MsgHead extends Serializable {

    /**
     * 微信用户
     *
     * @return 消息的接收用户
     */
    String getToUserName();

    /**
     * 微信用户
     *
     * @return 消息的发送用户
     */
    String getFromUserName();

    /**
     * 消息产生时间
     *
     * @return 时间
     */
    String getCreateTime();

    /**
     * 消息类型
     *
     * @return 类型
     */
    String getMsgType();

    /**
     * 事件类型
     *
     * @return 类型
     */
    String getEvent();
}
