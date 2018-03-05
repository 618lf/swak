package com.tmt.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.EventHandler;

public class MsgDataHandler2 implements EventHandler<MsgData> {

	private AtomicInteger count;
	public MsgDataHandler2(AtomicInteger count) {
		this.count = count;
	}
	
	@Override
	public void onEvent(MsgData event, long sequence, boolean endOfBatch) throws Exception {
		count.incrementAndGet();
	}
}
