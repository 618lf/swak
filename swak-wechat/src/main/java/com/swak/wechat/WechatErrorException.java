package com.swak.wechat;

import com.swak.exception.BaseRuntimeException;

/**
 * 微信交互过程中的错误
 * @author lifeng
 */
public class WechatErrorException extends BaseRuntimeException {

	private static final long serialVersionUID = 1L;

	public WechatErrorException(String msg) {
		super(msg);
	}
	public WechatErrorException(String msg, Throwable cause) {
		super(msg, cause);
	}
}