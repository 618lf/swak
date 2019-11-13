package com.swak.wechat.message;

import com.swak.utils.StringUtils;

public class AbstractEventMsg extends MsgHeadImpl implements ReqMsg {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMsgId() {
		return StringUtils.EMPTY;
	}
}
