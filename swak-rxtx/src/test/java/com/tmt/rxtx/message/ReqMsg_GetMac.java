package com.tmt.rxtx.message;

import com.swak.utils.StringUtils;
import com.tmt.rxtx.config.Config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 获取 mac 的请求
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class ReqMsg_GetMac extends ReqMsg {

	public ReqMsg_GetMac() {
		this.cmd = Config.CMD_GetMac;
		this.content = StringUtils.EMPTY;
	}

	public static ReqMsg_GetMac of() {
		return new ReqMsg_GetMac();
	}
}