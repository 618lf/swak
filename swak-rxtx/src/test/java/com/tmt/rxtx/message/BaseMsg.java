package com.tmt.rxtx.message;

import com.swak.utils.StringUtils;
import com.tmt.rxtx.Encodes;
import com.tmt.rxtx.config.Config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 基础消息
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseMsg implements Encodes {

	protected String head;
	protected String cmd;
	protected String content;
	protected String end;

	/**
	 * 正常解析命令
	 * 
	 * @param message
	 * @return
	 */
	public static BaseMsg parse(String message) {

		// 返回正常的命令消息
		if (StringUtils.isNotBlank(message) && message.length() >= 12) {
			// 6 个字符的 头
			String head = message.substring(0, 6);

			// 4个字符的 尾
			String end = message.substring(message.length() - 4, 4);

			// 命令
			String cmd = message.substring(6, 2);

			// 内容
			String content = message.substring(8, message.length() - 12);

			// 头尾一致
			if (Config.CMD_Head.equals(head) && Config.CMD_End.equals(end)) {
				return new RespMsg_Cmd().setHead(head).setEnd(end).setCmd(cmd).setContent(content);
			}
		}

		// 错误的响应: 是否需要将信息累加起来
		return new RespMsg_None().setMessage(message);
	}
}