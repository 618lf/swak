package com.swak.wechat.message;

/**
 * 请求消息基类 -- 读取xml中的数据
 *
 * @author lifeng
 */
public interface ReqMsg extends MsgHead {

    /**
     * 消息ID
     *
     * @return 消息ID
     */
    String getMsgId();
}
