package com.tmt.rxtx.message;

import com.swak.utils.time.DateTimes;
import com.tmt.rxtx.config.Config;

/**
 * 开始采集
 * 
 * @author lifeng
 */
public class ReqMsg_StartCollection extends ReqMsg {

	public ReqMsg_StartCollection() {
		this.cmd = Config.CMD_StartCollection;
		this.content = DateTimes.getFormatNow("yyyyMMddHHmmss");
	}

	public static ReqMsg_StartCollection of() {
		return new ReqMsg_StartCollection();
	}
}
