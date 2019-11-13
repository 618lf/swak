package com.swak.wechat.message;

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

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
}
