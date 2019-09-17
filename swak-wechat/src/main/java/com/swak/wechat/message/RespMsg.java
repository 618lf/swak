package com.swak.wechat.message;

/**
 * XML应答消息基类，应答消息跟基本消息没有任何区别，使用一个类方便识别及扩展。
 * 
 * 对于每一个POST请求，开发者在响应包（Get）中返回特定XML结构，对该消息进行响应 （现支持回复文本、图片、图文、语音、视频、音乐）。
 * 请注意，回复图片等多媒体消息时需要预先上传多媒体文件到微信服务器，只支持认证服务号。
 * 
 * 微信服务器在五秒内收不到响应会断掉连接， 如果在调试中，发现用户无法收到响应的消息，可以检查是否消息处理超时。
 * 
 * 注意：应答消息中，如不特殊说明，字段都是必填的。
 * @author lifeng
 */
public abstract class RespMsg extends MsgHead {

	private static final long serialVersionUID = 1L;

	public RespMsg() {}

	public RespMsg(String toUserName, String fromUserName, String msgType) {
		this.toUserName = toUserName;
		this.fromUserName = fromUserName;
		this.msgType = msgType;
	}

	/**
	 * 从请求消息中构建。应答消息的ToUserName与FromUserName与请求消息相反。
	 * 
	 * @param reqMsg
	 * @param createTime
	 * @param msgType
	 */
	public RespMsg(MsgHead reqMsg, String msgType) {
		this(reqMsg.fromUserName, reqMsg.toUserName, msgType);
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
}
