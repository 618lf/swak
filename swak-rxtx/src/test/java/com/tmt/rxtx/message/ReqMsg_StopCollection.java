package com.tmt.rxtx.message;

import com.swak.utils.StringUtils;
import com.tmt.rxtx.config.Config;

/**
 * 停止采集
 * 
 * @author lifeng
 */
public class ReqMsg_StopCollection extends ReqMsg {

	public ReqMsg_StopCollection() {
		this.cmd = Config.CMD_StopCollection;
		this.content = StringUtils.EMPTY;
	}

	public static ReqMsg_StopCollection of() {
		return new ReqMsg_StopCollection();
	}
}
