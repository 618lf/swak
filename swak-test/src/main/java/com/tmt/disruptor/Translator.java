package com.tmt.disruptor;

import java.nio.ByteBuffer;

import com.lmax.disruptor.EventTranslatorOneArg;

public class Translator implements EventTranslatorOneArg<MsgData, ByteBuffer> {

	@Override
	public void translateTo(MsgData event, long sequence, ByteBuffer data) {
		event.setMsg(data.getLong(0));
	}
}
