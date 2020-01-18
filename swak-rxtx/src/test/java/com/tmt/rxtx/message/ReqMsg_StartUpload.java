package com.tmt.rxtx.message;

import com.swak.utils.StringUtils;
import com.tmt.rxtx.config.Config;

/**
 * 开始采集
 * 
 * @author lifeng
 */
public class ReqMsg_StartUpload extends ReqMsg {

	public ReqMsg_StartUpload() {
		this.cmd = Config.CMD_StartUpload;
		this.content = StringUtils.EMPTY;
	}

	public static ReqMsg_StartUpload of() {
		return new ReqMsg_StartUpload();
	}
}
