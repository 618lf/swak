package com.tmt.rxtx.message;

import com.swak.utils.StringUtils;
import com.tmt.rxtx.config.Config;

/**
 * 开始采集
 * 
 * @author lifeng
 */
public class ReqMsg_StopUpload extends ReqMsg {

	public ReqMsg_StopUpload() {
		this.cmd = Config.CMD_StopUpload;
		this.content = StringUtils.EMPTY;
	}

	public static ReqMsg_StopUpload of() {
		return new ReqMsg_StopUpload();
	}
}
