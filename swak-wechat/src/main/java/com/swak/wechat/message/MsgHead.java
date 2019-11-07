package com.swak.wechat.message;

import java.io.Serializable;

/**
 * 消息头
 * 
 * @author lifeng
 */
public interface MsgHead extends Serializable {

	String getToUserName();

	String getFromUserName();

	String getCreateTime();

	String getMsgType();

	String getEvent();
}
