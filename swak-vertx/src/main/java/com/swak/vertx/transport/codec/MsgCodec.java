package com.swak.vertx.transport.codec;

import com.swak.serializer.SerializationUtils;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/**
 * 自定义的消息编解码器
 * 
 * @author lifeng
 */
public class MsgCodec implements MessageCodec<Msg, Msg> {

	/**
	 * 定义默认的名称
	 */
	public static final String CODEC_NAME = "msg-codec";

	@Override
	public void encodeToWire(Buffer buffer, Msg s) {
		buffer.appendBytes(SerializationUtils.serialize(s));
	}

	@Override
	public Msg decodeFromWire(int pos, Buffer buffer) {
		return (Msg) SerializationUtils.deserialize(buffer.getBytes());
	}

	/**
	 * 如果是本地消息则直接返回
	 */
	@Override
	public Msg transform(Msg s) {
		return s;
	}

	@Override
	public String name() {
		return CODEC_NAME;
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}
}
