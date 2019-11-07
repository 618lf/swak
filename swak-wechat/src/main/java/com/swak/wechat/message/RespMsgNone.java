package com.swak.wechat.message;

import com.swak.utils.StringUtils;

/**
 * 空消息
 * 
 * @author lifeng
 */
public enum RespMsgNone implements ReqMsg {
	INSTANCE;

	@Override
	public String getToUserName() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getFromUserName() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getCreateTime() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getMsgType() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getEvent() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getMsgId() {
		return StringUtils.EMPTY;
	}
}