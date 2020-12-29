package com.swak.paxos.transport;

import io.netty.channel.Channel;

/**
 * 消息
 * 
 * @author DELL
 */
public class Message {

	private byte[] receiveBuf;
	private int receiveLen;
	private boolean notifyMsg = false;
	private Channel channel;
	private long timeStamp;

	public static Message getNotifyNullMsg() {
		Message receiveMsg = new Message(null, 0, true);
		return receiveMsg;
	}

	public Message(byte[] receiveBuf, int receiveLen) {
		super();
		this.receiveBuf = receiveBuf;
		this.receiveLen = receiveLen;
	}

	public Message(byte[] receiveBuf, int receiveLen, boolean notifyMsg) {
		super();
		this.receiveBuf = receiveBuf;
		this.receiveLen = receiveLen;
		this.notifyMsg = notifyMsg;
	}

	public boolean isNotifyMsg() {
		return notifyMsg;
	}

	public void setNotifyMsg(boolean notifyMsg) {
		this.notifyMsg = notifyMsg;
	}

	public byte[] getReceiveBuf() {
		return receiveBuf;
	}

	public void setReceiveBuf(byte[] receiveBuf) {
		this.receiveBuf = receiveBuf;
	}

	public int getReceiveLen() {
		return receiveLen;
	}

	public void setReceiveLen(int receiveLen) {
		this.receiveLen = receiveLen;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
