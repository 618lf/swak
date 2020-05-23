package com.swak.wechat.message;

/**
 * 基本的响应消息
 *
 * @author: lifeng
 * @date: 2020/4/1 11:18
 */
public class AbstractRespMsg extends MsgHeadImpl implements RespMsg {

    private static final long serialVersionUID = 1L;

    public AbstractRespMsg() {
    }

    public AbstractRespMsg(String toUserName, String fromUserName, String msgType) {
        this.toUserName = toUserName;
        this.fromUserName = fromUserName;
        this.msgType = msgType;
    }

    public AbstractRespMsg(MsgHead reqMsg, String msgType) {
        this(reqMsg.getFromUserName(), reqMsg.getToUserName(), msgType);
    }

    @Override
	public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
