package com.tmt.rxtx.message;

import com.swak.utils.StringUtils;
import com.tmt.rxtx.config.Config;

/**
 * 请求数据：系统是请求端，设备是响应端
 * 
 * @author lifeng
 */
public class ReqMsg extends BaseMsg {

	/**
	 * 封装命令
	 */
	public ReqMsg() {
		this.head = Config.CMD_Head;
		this.end = Config.CMD_End;
	}

	/**
	 * 命令加密
	 * 
	 * @return
	 */
	public byte[] encode() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getHead());
		sb.append(this.getCmd());
		sb.append(StringUtils.defaultIfBlank(getContent(), StringUtils.EMPTY));
		sb.append(this.getEnd());
		return this.decodeHex(sb.toString());
	}
}
